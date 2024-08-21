package com.example.musicoblaka

import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.MediaStore

fun getLocalMusicArtists(context: Context): Set<String> {
    val artists = mutableSetOf<String>()
    val projection = arrayOf(MediaStore.Audio.Media.DATA)
    val cursor = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )

    cursor?.use {
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        while (cursor.moveToNext()) {
            val filePath = cursor.getString(dataColumn)
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(filePath)
                val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                if (artist != null) {
                    artists.add(artist)
                }
            } catch (e: Exception) {
                // Manejar errores si es necesario
            } finally {
                retriever.release()
            }
        }
    }

    return artists
}
