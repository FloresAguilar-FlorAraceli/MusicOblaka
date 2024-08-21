package com.example.musicoblaka

import Artista
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArtistAdapter(private val onItemClick: (Artista) -> Unit) : RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

    private var artists: List<Artista> = listOf()

    inner class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
        private val songCountTextView: TextView = itemView.findViewById(R.id.songCountTextView)
        private val albumArtImageView: ImageView = itemView.findViewById(R.id.albumArtImageView)

        fun bind(artista: Artista) {
            artistNameTextView.text = artista.name
            songCountTextView.text = "${artista.songCount * 1} Songs" // Multiplicado por 2

            // Cargar imagen del álbum
            val retriever = MediaMetadataRetriever()
            try {
                if (artista.filePath.isNotEmpty()) {
                    retriever.setDataSource(artista.filePath)
                    val albumArt = retriever.embeddedPicture
                    if (albumArt != null) {
                        val bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size)
                        albumArtImageView.setImageBitmap(bitmap)
                    } else {
                        albumArtImageView.setImageResource(R.drawable.placeholder_album_art)
                    }
                } else {
                    albumArtImageView.setImageResource(R.drawable.placeholder_album_art)
                }
            } catch (e: IllegalArgumentException) {
                Log.e("ArtistAdapter", "Error setting data source: ${e.message}")
                albumArtImageView.setImageResource(R.drawable.placeholder_album_art)
            } finally {
                retriever.release()
            }

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, CancionesActivity::class.java)
                intent.putExtra("ARTIST_NAME", artista.name)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(artists[position])
    }

    override fun getItemCount(): Int = artists.size

    fun submitList(list: List<Artista>) {
        artists = list
        notifyDataSetChanged()
    }
}
