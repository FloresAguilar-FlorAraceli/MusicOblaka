package com.example.musicoblaka

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class SongRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addSong(song: Song): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, song.title)
            put(DatabaseHelper.COLUMN_ARTIST, song.artist)
            put(DatabaseHelper.COLUMN_ALBUM, song.album)
            put(DatabaseHelper.COLUMN_DURATION, song.duration)
            put(DatabaseHelper.COLUMN_ALBUM_ART, song.albumArt)
            put(DatabaseHelper.COLUMN_GENRE, song.genre)
        }
        return db.insert(DatabaseHelper.TABLE_SONGS, null, values)
    }

    fun getAllSongs(): List<Song> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_SONGS,
            null,
            null,
            null,
            null,
            null,
            null
        )

        val songs = mutableListOf<Song>()
        with(cursor) {
            while (moveToNext()) {
                val song = Song(
                    id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    title = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                    artist = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ARTIST)),
                    album = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM)),
                    duration = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DURATION)),
                    albumArt = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_ART)),
                    favorite = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE)) == 1,
                    genre = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENRE))
                )
                songs.add(song)
            }
        }
        cursor.close()
        return songs
    }

    fun getFavoriteSongs(): List<Song> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_SONGS,
            null,
            "${DatabaseHelper.COLUMN_FAVORITE} = ?",
            arrayOf("1"),
            null,
            null,
            null
        )

        val songs = mutableListOf<Song>()
        with(cursor) {
            while (moveToNext()) {
                val song = Song(
                    id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    title = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                    artist = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ARTIST)),
                    album = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM)),
                    duration = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DURATION)),
                    albumArt = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ALBUM_ART)),
                    favorite = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_FAVORITE)) == 1,
                    genre = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENRE))
                )
                songs.add(song)
            }
        }
        cursor.close()
        return songs
    }

    fun updateFavoriteStatus(songId: Long, isFavorite: Boolean): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_FAVORITE, if (isFavorite) 1 else 0)
        }
        return db.update(
            DatabaseHelper.TABLE_SONGS,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(songId.toString())
        )
    }
}
