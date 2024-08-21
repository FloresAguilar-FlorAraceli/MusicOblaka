package com.example.musicoblaka

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs_in_playlist")
data class SongInPlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: Long,
    val songPath: String
)
