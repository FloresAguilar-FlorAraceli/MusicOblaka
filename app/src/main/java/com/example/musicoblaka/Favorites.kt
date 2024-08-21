package com.example.musicoblaka

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Favorites : AppCompatActivity() {
    private lateinit var favoritesList: ListView
    private lateinit var songRepository: SongRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

                favoritesList = findViewById(R.id.favorites_list)
                songRepository = SongRepository(this)

                val favoriteSongs = songRepository.getFavoriteSongs()
                val songTitles = favoriteSongs.map { it.title }

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, songTitles)
                favoritesList.adapter = adapter

                // Handle item clicks to play song or show details
            }
        }


