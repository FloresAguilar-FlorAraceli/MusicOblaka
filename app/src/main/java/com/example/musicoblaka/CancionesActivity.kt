package com.example.musicoblaka

import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CancionesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SongAdapter
    private lateinit var musicPlayerManager: MusicPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canciones)


    recyclerView = findViewById(R.id.songsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = SongAdapter { songPath ->
            // Handle song click, e.g., start playing the song
            musicPlayerManager.playSong(songPath)
        }
        recyclerView.adapter = adapter

        musicPlayerManager = MusicPlayerManager.getInstance(this)

        val artistName = intent.getStringExtra("ARTIST_NAME") ?: "Unknown Artist"
        findViewById<TextView>(R.id.artistNameTextView).text = artistName

        loadSongs(artistName)
    }

    private fun loadSongs(artistName: String) {
        val songs = mutableListOf<String>()
        val songList = musicPlayerManager.songList

        songList.forEach { songPath ->
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(songPath)
            }
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
            retriever.release()

            if (artist == artistName) {
                songs.add(songPath)
            }
        }

        adapter.submitList(songs)
    }
}
