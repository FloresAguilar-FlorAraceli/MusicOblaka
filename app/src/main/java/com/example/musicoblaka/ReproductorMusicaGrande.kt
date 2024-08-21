package com.example.musicoblaka

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ReproductorMusicaGrande : AppCompatActivity(), MusicPlayerManager.MusicPlayerObserver {

    private lateinit var musicFile: MusicFile // Archivo de música actual
    private lateinit var musicPlayerManager: MusicPlayerManager // Gestor de reproducción

    // Vistas
    private lateinit var songTitleTextView: TextView // Título
    private lateinit var songArtistTextView: TextView // Artista
    private lateinit var songElapsedTimeTextView: TextView // Tiempo transcurrido
    private lateinit var playPauseButton: ImageButton // Reproducir/Pausar
    private lateinit var nextButton: ImageButton // Siguiente
    private lateinit var previousButton: ImageButton // Anterior
    private lateinit var seekBar: SeekBar // Barra de progreso
    private lateinit var albumArtImageView: ImageView // Carátula del álbum
    private lateinit var randomButton: ImageButton // Aleatorio
    private lateinit var repeatButton: ImageButton // Repetir
    private lateinit var favoritesButton: ImageButton // Favorito

    companion object {
        const val EXTRA_MUSIC_FILE = "EXTRA_MUSIC_FILE" // Clave para el archivo de música
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reproductor_musica_grande) // Diseño

        musicFile = intent.getParcelableExtra(EXTRA_MUSIC_FILE)
            ?: throw IllegalArgumentException("MusicFile cannot be null") // Archivo de música

        initializeViews() // Inicializa vistas
        musicPlayerManager = MusicPlayerManager.getInstance(this) // Inicializa gestor

        musicPlayerManager.initialize(
            songTitleTextView, songArtistTextView, songElapsedTimeTextView,
            playPauseButton, seekBar, randomButton, repeatButton, favoritesButton,
            albumArtImageView, nextButton, previousButton
        )

        musicPlayerManager.registerObserver(this) // Registra observador

        setupUI() // Configura interfaz y comienza a reproducir
    }

    private fun initializeViews() {
        // Enlaza vistas
        songTitleTextView = findViewById(R.id.song_title)
        songArtistTextView = findViewById(R.id.song_artist)
        songElapsedTimeTextView = findViewById(R.id.song_elapsed_time)
        playPauseButton = findViewById(R.id.play_pause_button)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.previous_button)
        seekBar = findViewById(R.id.song_progress)
        albumArtImageView = findViewById(R.id.album_art)
        randomButton = findViewById(R.id.random_button)
        repeatButton = findViewById(R.id.repeat_button)
        favoritesButton = findViewById(R.id.favorites_button)
    }

    private fun setupUI() {
        musicPlayerManager.playMusic(musicFile.path) // Reproduce música
        updateSongInfo(musicFile.path) // Actualiza info de la canción
        musicPlayerManager.updateSeekBar() // Actualiza barra de progreso

        playPauseButton.setOnClickListener {
            if (musicPlayerManager.isPlaying) {
                musicPlayerManager.pauseMusic() // Pausa música
                playPauseButton.setImageResource(R.drawable.ic_play)
            } else {
                musicPlayerManager.playMusic(musicFile.path) // Reproduce música
                playPauseButton.setImageResource(R.drawable.ic_pause)
            }
        }

        nextButton.setOnClickListener { musicPlayerManager.goToNextSong() } // Siguiente
        previousButton.setOnClickListener { musicPlayerManager.goToPreviousSong() } // Anterior
        randomButton.setOnClickListener { musicPlayerManager.toggleRandom() } // Aleatorio
        repeatButton.setOnClickListener { musicPlayerManager.toggleRepeat() } // Repetir
        favoritesButton.setOnClickListener { musicPlayerManager.toggleFavorites() } // Favorito
    }

    private fun updateSongInfo(filePath: String) {
        val retriever = MediaMetadataRetriever().apply {
            setDataSource(filePath) // Configura fuente de datos
        }

        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: musicFile.title
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: musicFile.artist
        val albumArt = retriever.embeddedPicture // Carátula del álbum

        songTitleTextView.text = title // Muestra título
        songArtistTextView.text = artist // Muestra artista
        if (albumArt != null) {
            Glide.with(this)
                .asBitmap()
                .load(albumArt)
                .placeholder(R.drawable.placeholder_album_art)
                .into(albumArtImageView) // Muestra carátula
        } else {
            albumArtImageView.setImageResource(R.drawable.placeholder_album_art) // Recurso por defecto
        }

        retriever.release() // Libera recursos
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayerManager.unregisterObserver(this) // Desregistra observador
        musicPlayerManager.release() // Libera recursos
    }

    // Métodos del observador
    override fun onSongChanged(songPath: String) {
        updateSongInfo(songPath) // Actualiza info al cambiar canción
    }

    override fun onPlayPause(isPlaying: Boolean) {
        playPauseButton.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play) // Actualiza botón
    }

    override fun onProgressChanged(progress: Int) {
        seekBar.progress = progress // Actualiza barra de progreso
    }
}
