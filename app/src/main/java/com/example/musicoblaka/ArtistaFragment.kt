package com.example.musicoblaka

import Artista
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ArtistaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ArtistAdapter
    private lateinit var musicPlayerManager: MusicPlayerManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_artista_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ArtistAdapter { artista ->
            musicPlayerManager.bind(artista)
        }
        recyclerView.adapter = adapter

        musicPlayerManager = MusicPlayerManager.getInstance(requireContext())
        loadArtists()
    }

    private fun loadArtists() {
        val artists = mutableListOf<Artista>()
        val songList = musicPlayerManager.songList

        val artistMap = mutableMapOf<String, Pair<Int, String?>>()
        val albumMap = mutableMapOf<String, String>()

        songList.forEach { songPath ->
            val retriever = MediaMetadataRetriever().apply {
                setDataSource(songPath)
            }
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
            val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown Album"
            retriever.release()

            // Store the album for each artist
            if (artistMap[artist] == null) {
                albumMap[artist] = album
            }

            val current = artistMap[artist] ?: Pair(0, songPath)
            artistMap[artist] = Pair(current.first + 1, current.second)
        }

        artistMap.forEach { (artistName, pair) ->
            val album = albumMap[artistName] ?: "Unknown Album"
            artists.add(Artista(name = artistName, album = album, songCount = pair.first, filePath = pair.second ?: ""))
        }

        adapter.submitList(artists)
    }
}

