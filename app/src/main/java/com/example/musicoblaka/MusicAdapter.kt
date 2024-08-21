package com.example.musicoblaka

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.concurrent.TimeUnit

class MusicAdapter(
    private val musicList: List<MusicFile>, // Lista de archivos de música que se mostrará en el RecyclerView
    private val onSongClick: (MusicFile) -> Unit // Función que se llama cuando se hace clic en una canción
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        // Infla el diseño para cada elemento del RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_music, parent, false)
        return MusicViewHolder(view) // Crea una instancia de MusicViewHolder con el diseño inflado
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        // Obtiene el archivo de música para la posición actual
        val musicFile = musicList[position]
        // Configura los datos en las vistas del ViewHolder
        holder.titleTextView.text = musicFile.title
        holder.artistTextView.text = musicFile.artist
        holder.durationTextView.text = formatDuration(musicFile.duration)

        // Muestra la carátula del álbum si está disponible, usa un placeholder si no
        musicFile.albumArtUri?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .placeholder(R.drawable.placeholder_album_art)
                .into(holder.albumArtImageView)
        } ?: holder.albumArtImageView.setImageResource(R.drawable.placeholder_album_art)

        // Configura el clic en el elemento del RecyclerView
        holder.itemView.setOnClickListener {
            onSongClick(musicFile) // Llama a la función onSongClick con el archivo de música clicado
        }
    }

    override fun getItemCount(): Int = musicList.size // Retorna el tamaño de la lista de música

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias a las vistas dentro del diseño del item
        val titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
        val artistTextView: TextView = itemView.findViewById(R.id.artist_text_view)
        val albumArtImageView: ImageView = itemView.findViewById(R.id.album_art)
        val durationTextView: TextView = itemView.findViewById(R.id.duration_text_view)
    }

    private fun formatDuration(durationMillis: Long): String {
        // Formatea la duración en milisegundos a un formato de minutos y segundos
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
