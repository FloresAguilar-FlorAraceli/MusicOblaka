package com.example.musicoblaka

interface PlaylistApi {
    suspend fun createPlaylist(name: String): Playlist
    suspend fun getPlaylist(id: Long): Playlist?
    suspend fun getAllPlaylists(): List<Playlist>
    suspend fun addSongToPlaylist(playlistId: Long, songPath: String): Boolean
    suspend fun removeSongFromPlaylist(playlistId: Long, songPath: String): Boolean
    suspend fun deletePlaylist(id: Long): Boolean
}
