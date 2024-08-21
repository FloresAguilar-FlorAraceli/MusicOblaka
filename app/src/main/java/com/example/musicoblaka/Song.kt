package com.example.musicoblaka

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int,
    val albumArt: String,
    val favorite: Boolean,
    val genre: String
)
