package com.example.musicoblaka

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "musicplayer.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_SONGS = "songs"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ARTIST = "artist"
        const val COLUMN_ALBUM = "album"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_ALBUM_ART = "album_art"
        const val COLUMN_FAVORITE = "favorite"
        const val COLUMN_GENRE = "genre"

        private const val CREATE_TABLE_SONGS = (
                "CREATE TABLE $TABLE_SONGS (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_TITLE TEXT, " +
                        "$COLUMN_ARTIST TEXT, " +
                        "$COLUMN_ALBUM TEXT, " +
                        "$COLUMN_DURATION INTEGER, " +
                        "$COLUMN_ALBUM_ART TEXT, " +
                        "$COLUMN_FAVORITE INTEGER DEFAULT 0, " +
                        "$COLUMN_GENRE TEXT)"
                )
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_SONGS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SONGS")
        onCreate(db)
    }
}
