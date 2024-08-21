package com.example.musicoblaka

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), SongClickListener, MusicPlayerManager.MusicPlayerObserver {

    private lateinit var musicPlayerManager: MusicPlayerManager
    private lateinit var songTitleTextView: TextView
    private lateinit var songArtistTextView: TextView
    private lateinit var songElapsedTimeTextView: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var randomButton: ImageButton
    private lateinit var repeatButton: ImageButton
    private lateinit var favoritesButton: ImageButton
    private lateinit var albumArtImageView: ImageView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val REQUEST_CODE_READ_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asegúrate de que el layout es correcto
        initializeViews() // Llama a initializeViews después de setContentView

    initializeViews()
        setupViewPager()

        musicPlayerManager = MusicPlayerManager.getInstance(this)
        musicPlayerManager.initialize(
            songTitleTextView, songArtistTextView, songElapsedTimeTextView,
            playPauseButton, seekBar, randomButton, repeatButton, favoritesButton,
            albumArtImageView, nextButton, previousButton
        )

        musicPlayerManager.registerObserver(this)
        checkAndRequestPermissions()

        supportActionBar?.hide()
        viewPager.setPageTransformer(AnimacionPageTransformer())
    }

    private fun initializeViews() {
        songTitleTextView = findViewById(R.id.song_title)
        songArtistTextView = findViewById(R.id.song_artist)
        songElapsedTimeTextView = findViewById(R.id.song_elapsed_time)
        playPauseButton = findViewById(R.id.play_pause_button)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.previous_button)
        seekBar = findViewById(R.id.song_progress)
        randomButton = findViewById(R.id.random_button)
        repeatButton = findViewById(R.id.repeat_button)
        favoritesButton = findViewById(R.id.favorites_button)
        albumArtImageView = findViewById(R.id.album_art)
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        playPauseButton.setOnClickListener {
            if (musicPlayerManager.isPlaying) {
                musicPlayerManager.pauseMusic()
            } else {
                musicPlayerManager.currentSong?.let { song ->
                    musicPlayerManager.playMusic(song)
                } ?: run {
                    Toast.makeText(this, "No song selected", Toast.LENGTH_SHORT).show()
                }
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicPlayerManager.mediaPlayer?.seekTo(progress)
                    updateElapsedTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        previousButton.setOnClickListener {
            musicPlayerManager.goToPreviousSong()
        }

        nextButton.setOnClickListener {
            musicPlayerManager.goToNextSong()
        }

        favoritesButton.setOnClickListener {
            musicPlayerManager.toggleFavorites()
            updateFavoritesButtonIcon()
        }

        randomButton.setOnClickListener {
            musicPlayerManager.toggleRandom()
        }

        repeatButton.setOnClickListener {
            musicPlayerManager.toggleRepeat()
        }
    }

    private fun setupViewPager() {
        val adapter = MainViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.setIcon(R.drawable.a_ic_inicio)
                1 -> tab.setIcon(R.drawable.a_ic_lista)
                2 -> tab.setIcon(R.drawable.a_ic_favorite)
                3 -> tab.setIcon(R.drawable.a_ic_artistas)
            }
        }.attach()
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permiso si aún no se ha concedido
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_STORAGE)
            } else {
                // Permiso ya concedido, acceder a los archivos de música
                accessMusicFiles()
            }
        } else {
            // En versiones anteriores, el permiso ya está concedido por defecto
            accessMusicFiles()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, acceder a los archivos de música
                accessMusicFiles()
            } else {
                // Permiso denegado, mostrar un mensaje al usuario
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun accessMusicFiles() {
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)

        cursor?.use {
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val songPaths = mutableListOf<String>()
            while (it.moveToNext()) {
                songPaths.add(it.getString(dataIndex))
            }
            musicPlayerManager.updateSongList(songPaths)
        }

        if (musicPlayerManager.songList.isEmpty()) {
            Toast.makeText(this, "No music files found", Toast.LENGTH_SHORT).show()
        } else {
            musicPlayerManager.currentSongIndex = 0
            musicPlayerManager.playMusic(musicPlayerManager.songList[musicPlayerManager.currentSongIndex])
        }
    }

    override fun onSongClick(filePath: String) {
        musicPlayerManager.playMusic(filePath)
    }

    private fun updateFavoritesButtonIcon() {
        val currentSongPath = musicPlayerManager.currentSong
        val iconResId = if (currentSongPath != null && musicPlayerManager.isFavorite(currentSongPath)) {
            R.drawable.ic_favorite2
        } else {
            R.drawable.ic_favorite_active
        }
        favoritesButton.setImageResource(iconResId)
    }



    override fun onSongChanged(songPath: String) {
        updateSongInfo(songPath)
    }

    override fun onPlayPause(isPlaying: Boolean) {
        playPauseButton.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
    }

    override fun onProgressChanged(progress: Int) {
        seekBar.progress = progress
        updateElapsedTime(progress)
    }

    private fun updateSongInfo(filePath: String) {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        try {
            mediaMetadataRetriever.setDataSource(filePath)
            val albumArt = mediaMetadataRetriever.embeddedPicture
            val albumArtBitmap = if (albumArt != null) BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size) else null
            albumArtImageView.setImageBitmap(albumArtBitmap ?: BitmapFactory.decodeResource(resources, R.drawable.placeholder_album_art))
            songTitleTextView.text = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown Title"
            songArtistTextView.text = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaMetadataRetriever.release()
        }
    }

    private fun updateElapsedTime(progress: Int) {
        val minutes = (progress / 60000).toString().padStart(2, '0')
        val seconds = ((progress % 60000) / 1000).toString().padStart(2, '0')
        songElapsedTimeTextView.text = "$minutes:$seconds"
    }
}
