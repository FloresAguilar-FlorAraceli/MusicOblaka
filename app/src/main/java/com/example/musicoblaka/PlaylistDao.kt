package com.example.musicoblaka
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
@Dao
interface PlaylistDao {

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity?

    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Insert
    suspend fun insertSongInPlaylist(songInPlaylist: SongInPlaylistEntity)

    @Query("DELETE FROM songs_in_playlist WHERE playlistId = :playlistId AND songPath = :songPath")
    suspend fun deleteSongFromPlaylist(playlistId: Long, songPath: String)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("SELECT songPath FROM songs_in_playlist WHERE playlistId = :playlistId")
    suspend fun getSongsInPlaylist(playlistId: Long): List<String>
}
