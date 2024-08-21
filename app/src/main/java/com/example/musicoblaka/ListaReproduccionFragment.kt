package com.example.musicoblaka

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListaReproduccionFragment : Fragment(R.layout.activity_lista_reproduccion_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var musicAdapter: MusicAdapter
    private val musicList = mutableListOf<MusicFile>()
    private var songClickListener: SongClickListener? = null
    private lateinit var musicPlayerManager: MusicPlayerManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el diseño del fragmento y configura el RecyclerView
        val view = inflater.inflate(R.layout.activity_lista_reproduccion_fragment, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        musicAdapter = MusicAdapter(musicList) { musicFile ->
            openReproductorActivity(musicFile) // Maneja el clic en una canción
        }
        recyclerView.adapter = musicAdapter

        musicPlayerManager = MusicPlayerManager.getInstance(requireContext())

        // Verifica y solicita permisos para leer el almacenamiento externo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_CODE)
            } else {
                loadMusicFiles() // Carga los archivos de música si ya se tienen permisos
            }
        } else {
            loadMusicFiles() // Carga los archivos de música en versiones anteriores a Android M
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SongClickListener) {
            songClickListener = context
        } else {
            throw RuntimeException("$context debe implementar SongClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        songClickListener = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMusicFiles() // Carga los archivos de música si se concede el permiso
            } else {
                Toast.makeText(context, "Permiso denegado para leer el almacenamiento externo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMusicFiles() {
        // Carga los archivos de música desde el almacenamiento externo
        val musicUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID // Para obtener la carátula del álbum
        )

        val cursor: Cursor? = context?.contentResolver?.query(
            musicUri,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val data = it.getString(dataColumn)
                val duration = it.getLong(durationColumn)
                val albumId = it.getLong(albumIdColumn)
                val albumArtUri = getAlbumArtUri(albumId)

                val musicFile = MusicFile(title, data, artist, albumArtUri, duration)
                musicList.add(musicFile) // Agrega el archivo de música a la lista
            }
        }

        musicAdapter.notifyDataSetChanged() // Notifica al adaptador sobre los cambios en la lista
    }

    private fun getAlbumArtUri(albumId: Long): Uri? {
        // Genera la URI para la carátula del álbum basada en el ID del álbum
        val albumArtUri = Uri.parse("content://media/external/audio/albumart")
        return Uri.withAppendedPath(albumArtUri, albumId.toString())
    }

    private fun openReproductorActivity(musicFile: MusicFile) {
        // Abre la actividad del reproductor de música grande con el archivo de música como extra
        val intent = Intent(requireContext(), ReproductorMusicaGrande::class.java).apply {
            putExtra(ReproductorMusicaGrande.EXTRA_MUSIC_FILE, musicFile)
        }
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1
    }
}
