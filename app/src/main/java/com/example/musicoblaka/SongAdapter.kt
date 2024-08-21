package com.example.musicoblaka

import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(private val onSongClick: (String) -> Unit) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var songs = listOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val songPath = songs[position]
        holder.bind(songPath)
        holder.itemView.setOnClickListener { onSongClick(songPath) }
    }

    override fun getItemCount(): Int = songs.size

    fun submitList(newSongs: List<String>) {
        songs = newSongs
        notifyDataSetChanged()
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songTitleTextView: TextView = itemView.findViewById(R.id.songNameTextView)

        fun bind(songPath: String) {
            // Extract metadata or set a default name for the song
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(songPath)
            }
            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "Unknown Title"
            songTitleTextView.text = title
            retriever.release()
        }
    }
}
