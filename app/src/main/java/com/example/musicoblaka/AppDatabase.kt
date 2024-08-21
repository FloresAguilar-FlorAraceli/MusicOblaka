package com.example.musicoblaka

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlaylistEntity::class, SongInPlaylistEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
}
