package com.example.musicoblaka

import Artista
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Handler
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.io.IOException

class MusicPlayerManager private constructor(private val context: Context) {

    private val favoritesApi = FavoritesApi(context)
    private val handler = Handler()
    var currentSong: String? = null

    var mediaPlayer: MediaPlayer? = null
    var isPlaying: Boolean = false
    var isRandom: Boolean = false
    var isRepeat: Boolean = false
    var currentSongIndex = -1
    private var _songList: MutableList<String> = mutableListOf()

    var songList: List<String>
        get() = _songList
        set(value) {
            _songList.clear()
            _songList.addAll(value)
        }

    lateinit var songTitleTextView: TextView
    lateinit var songArtistTextView: TextView
    lateinit var songElapsedTimeTextView: TextView
    lateinit var playPauseButton: ImageButton
    lateinit var nextButton: ImageButton
    lateinit var previousButton: ImageButton
    lateinit var seekBar: SeekBar
    lateinit var randomButton: ImageButton
    lateinit var repeatButton: ImageButton
    lateinit var favoritesButton: ImageButton
    lateinit var albumArtImageView: ImageView

    private val observers = mutableListOf<MusicPlayerObserver>()

    interface MusicPlayerObserver {
        fun onSongChanged(songPath: String)
        fun onPlayPause(isPlaying: Boolean)
        fun onProgressChanged(progress: Int)
    }

    init {
        loadFavorites()
    }
    fun playSong(songPath: String) {
        playMusic(songPath) // Llama a playMusic
    }

    fun registerObserver(observer: MusicPlayerObserver) {
        observers.add(observer)
    }

    fun unregisterObserver(observer: MusicPlayerObserver) {
        observers.remove(observer)
    }

    private fun notifySongChanged(songPath: String) {
        observers.forEach { it.onSongChanged(songPath) }
    }

    private fun notifyPlayPause(isPlaying: Boolean) {
        observers.forEach { it.onPlayPause(isPlaying) }
    }

    private fun notifyProgressChanged(progress: Int) {
        observers.forEach { it.onProgressChanged(progress) }
    }

    fun updateSongList(newSongList: List<String>) {
        _songList.clear()
        _songList.addAll(newSongList)
    }

    fun initialize(
        songTitle: TextView, songArtist: TextView, songElapsedTime: TextView,
        playPauseBtn: ImageButton, seekBar: SeekBar,
        randomBtn: ImageButton, repeatBtn: ImageButton, favoritesBtn: ImageButton,
        albumArt: ImageView, nextBtn: ImageButton, previousBtn: ImageButton
    ) {
        songTitleTextView = songTitle
        songArtistTextView = songArtist
        songElapsedTimeTextView = songElapsedTime
        playPauseButton = playPauseBtn
        this.seekBar = seekBar
        randomButton = randomBtn
        repeatButton = repeatBtn
        favoritesButton = favoritesBtn
        albumArtImageView = albumArt
        nextButton = nextBtn
        previousButton = previousBtn

        randomButton.setOnClickListener { toggleRandom() }
        repeatButton.setOnClickListener { toggleRepeat() }
        favoritesButton.setOnClickListener { toggleFavorites() }
        nextButton.setOnClickListener { goToNextSong() }
        previousButton.setOnClickListener { goToPreviousSong() }
        playPauseButton.setOnClickListener {
            if (isPlaying) {
                pauseMusic()
            } else {
                songList.getOrNull(currentSongIndex)?.let {
                    playMusic(it)
                }
            }
        }

        updateFavoritesIcon()
    }

    fun playMusic(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            try {
                mediaPlayer?.reset() ?: run {
                    mediaPlayer = MediaPlayer()
                }
                mediaPlayer?.apply {
                    setDataSource(filePath)
                    prepare()
                    start()
                }

                currentSong = filePath // Asignar aquí

                val retriever = MediaMetadataRetriever().apply {
                    setDataSource(filePath)
                }

                val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.name
                val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
                val duration = mediaPlayer?.duration?.let {
                    String.format("%02d:%02d", it / 60000, (it / 1000) % 60)
                } ?: "00:00"

                val songCount = getSongCountForArtist(artist) // Debe devolver un Int
                val artista = Artista(title, artist, songCount, filePath)

                isPlaying = true
                playPauseButton.setImageResource(R.drawable.ic_pause)

                retriever.release()

                seekBar.max = mediaPlayer?.duration ?: 0
                updateSeekBar()
                updateFavoritesIcon()

                mediaPlayer?.setOnCompletionListener {
                    isPlaying = false
                    playPauseButton.setImageResource(R.drawable.ic_play)
                    when {
                        isRepeat -> playMusic(filePath)
                        isRandom -> playRandomSong()
                        else -> goToNextSong()
                    }
                }

                notifySongChanged(filePath)

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error playing file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show()
        }
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
        isPlaying = false
        playPauseButton.setImageResource(R.drawable.ic_play)
        notifyPlayPause(false)
    }

    fun updateSeekBar() {
        mediaPlayer?.let { player ->
            seekBar.progress = player.currentPosition
            updateElapsedTime(player.currentPosition)
            notifyProgressChanged(player.currentPosition)
            if (player.isPlaying) {
                handler.postDelayed({ updateSeekBar() }, 1000)
            }
        }
    }

    fun updateElapsedTime(currentPosition: Int) {
        val elapsedTime = String.format("%02d:%02d", (currentPosition / 60000), (currentPosition / 1000) % 60)
        songElapsedTimeTextView.text = elapsedTime
    }

    fun goToPreviousSong() {
        if (songList.isNotEmpty()) {
            currentSongIndex = if (currentSongIndex > 0) {
                currentSongIndex - 1
            } else if (isRepeat) {
                songList.size - 1
            } else {
                return
            }
            playMusic(songList[currentSongIndex])
        }
    }

    fun goToNextSong() {
        if (songList.isNotEmpty()) {
            currentSongIndex = if (currentSongIndex < songList.size - 1) {
                currentSongIndex + 1
            } else if (isRepeat) {
                0
            } else {
                return
            }
            playMusic(songList[currentSongIndex])
        }
    }

    fun playRandomSong() {
        if (songList.isNotEmpty()) {
            currentSongIndex = (songList.indices).random()
            playMusic(songList[currentSongIndex])
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }

    fun toggleRandom() {
        isRandom = !isRandom
        randomButton.setImageResource(if (isRandom) R.drawable.ic_random_active else R.drawable.ic_random)
    }

    fun toggleRepeat() {
        isRepeat = !isRepeat
        repeatButton.setImageResource(if (isRepeat) R.drawable.ic_repeat_current else R.drawable.ic_repeat)
    }

    private fun loadFavorites() {
        val favorites = favoritesApi.getFavorites()
        _songList.clear()
        _songList.addAll(favorites)
    }

    fun isFavorite(songPath: String): Boolean {
        return favoritesApi.isFavorite(songPath)
    }

    fun toggleFavorites() {
        currentSong?.let {
            if (favoritesApi.isFavorite(it)) {
                favoritesApi.removeFavorite(it)
                Toast.makeText(context, "$it Eliminado de Favoritos", Toast.LENGTH_SHORT).show()
            } else {
                favoritesApi.addFavorite(it)
                Toast.makeText(context, "$it Añadida a Favoritos", Toast.LENGTH_SHORT).show()
            }
            updateFavoritesIcon()
        } ?: run {
            Toast.makeText(context, "No se está reproduciendo ninguna canción actualmente.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFavoritesIcon() {
        currentSong?.let {
            val isFavorite = favoritesApi.isFavorite(it)
            favoritesButton.setImageResource(if (isFavorite) R.drawable.ic_favorite_active else R.drawable.ic_favorite2)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MusicPlayerManager? = null

        fun getInstance(context: Context): MusicPlayerManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MusicPlayerManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    //artista

    fun bind(artista: Artista) {
        songTitleTextView.text = artista.name
        songArtistTextView.text = artista.album
        songElapsedTimeTextView.text = "${artista.songCount} Songs"

        // Si quieres mostrar una imagen de álbum:
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(artista.filePath) // Asegúrate de que tengas el path del archivo
        val albumArt = retriever.embeddedPicture
        if (albumArt != null) {
            val bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size)
            albumArtImageView.setImageBitmap(bitmap)
        } else {
            albumArtImageView.setImageResource(R.drawable.placeholder_album_art)
        }
        retriever.release()
    }

    fun getSongCountForArtist(artistName: String): Int {
        return _songList.count { songPath ->
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(songPath)
            }
            val songArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            retriever.release()
            songArtist == artistName
        }
    }


}
