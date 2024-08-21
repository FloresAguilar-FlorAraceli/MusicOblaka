package com.example.musicoblaka

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Genres : AppCompatActivity() {
    private lateinit var genresGrid: GridView
    private lateinit var songRepository: SongRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_genres)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

                val songs = songRepository.getAllSongs()
                val genres = songs.map { it.genre }.distinct()

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, genres)
                genresGrid.adapter = adapter

                // Handle item clicks to show songs of the selected genre
            }
        }


