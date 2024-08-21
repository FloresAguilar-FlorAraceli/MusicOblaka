package com.example.musicoblaka

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
class PlaylistApiImpl(private val context: Context) : PlaylistApi {

    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "music_database"
    ).build()

    override suspend fun createPlaylist(name: String): Playlist = withContext(Dispatchers.IO) {
        val playlistEntity = PlaylistEntity(name = name)
        val id = db.playlistDao().insertPlaylist(playlistEntity)
        Playlist(id, name)
    }

    override suspend fun addSongToPlaylist(playlistId: Long, songPath: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            db.playlistDao().insertSongInPlaylist(SongInPlaylistEntity(playlistId = playlistId, songPath = songPath))
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getPlaylist(id: Long): Playlist? = withContext(Dispatchers.IO) {
        val playlistEntity = db.playlistDao().getPlaylistById(id)
        return@withContext playlistEntity?.let {
            Playlist(it.id, it.name, db.playlistDao().getSongsInPlaylist(it.id).toMutableList())
        }
    }

    override suspend fun getAllPlaylists(): List<Playlist> = withContext(Dispatchers.IO) {
        db.playlistDao().getAllPlaylists().map {
            Playlist(it.id, it.name, db.playlistDao().getSongsInPlaylist(it.id).toMutableList())
        }
    }

    override suspend fun removeSongFromPlaylist(playlistId: Long, songPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            db.playlistDao().deleteSongFromPlaylist(playlistId, songPath)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deletePlaylist(id: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            val playlist = db.playlistDao().getPlaylistById(id) ?: return@withContext false
            db.playlistDao().deletePlaylist(playlist)
            true
        } catch (e: Exception) {
            false
        }
    }
}
