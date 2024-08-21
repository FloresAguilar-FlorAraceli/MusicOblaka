package com.example.musicoblaka

data class Playlist(
    val id: Long,
    val name: String,
    val songPaths: MutableList<String> = mutableListOf()
)
