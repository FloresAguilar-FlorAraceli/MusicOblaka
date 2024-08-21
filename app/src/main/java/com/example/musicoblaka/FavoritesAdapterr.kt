package com.example.musicoblaka

import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class FavoritesAdapterr(private var favoritesList: List<String>) : RecyclerView.Adapter<FavoritesAdapterr.FavoritesViewHolder>() {

    class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumArtImageView: ImageView = itemView.findViewById(R.id.album_art)
        val songTitleTextView: TextView = itemView.findViewById(R.id.song_title)
        val songDurationTextView: TextView = itemView.findViewById(R.id.song_duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_song, parent, false)
        return FavoritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val favoriteSongPath = favoritesList[position]
        val file = File(favoriteSongPath)

        // Cargar la carátula del álbum utilizando Glide
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(favoriteSongPath)
        val albumArt = retriever.embeddedPicture
        if (albumArt != null) {
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(albumArt)
                .placeholder(R.drawable.placeholder_album_art)
                .into(holder.albumArtImageView)
        } else {
            holder.albumArtImageView.setImageResource(R.drawable.placeholder_album_art)
        }

        // Configurar el título de la canción
        holder.songTitleTextView.text = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: file.nameWithoutExtension

        // Configurar la duración de la canción
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
        retriever.release()
        if (duration != null) {
            holder.songDurationTextView.text = String.format("%02d:%02d", (duration / 1000) / 60, (duration / 1000) % 60)
        } else {
            holder.songDurationTextView.text = "Unknown"
        }
    }

    override fun getItemCount(): Int {
        return favoritesList.size
    }

    fun updateFavorites(newFavoritesList: List<String>) {
        favoritesList = newFavoritesList
        notifyDataSetChanged()
    }
}
