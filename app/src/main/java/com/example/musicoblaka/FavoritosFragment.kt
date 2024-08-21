package com.example.musicoblaka

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritosFragment : Fragment(), MusicPlayerManager.MusicPlayerObserver {

    private lateinit var favoritesAdapter: FavoritesAdapterr
    private lateinit var favoritesApi: FavoritesApi
    private var favoritesList: List<String> = listOf()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_favoritos_fragment, container, false)
        recyclerView = view.findViewById(R.id.favorites_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inicializar el adaptador con una lista vacía
        favoritesAdapter = FavoritesAdapterr(favoritesList)
        recyclerView.adapter = favoritesAdapter

        // Inicializar FavoritesApi
        favoritesApi = FavoritesApi(requireContext())

        // Inicializar y registrar como observador
        MusicPlayerManager.getInstance(requireContext()).registerObserver(this)

        // Obtener y actualizar la lista de canciones favoritas
        updateFavoritesList()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Desregistrar al observador para evitar fugas de memoria
        MusicPlayerManager.getInstance(requireContext()).unregisterObserver(this)
    }

    override fun onSongChanged(songPath: String) {
        // Actualizar la lista si es necesario
        updateFavoritesList()
    }

    override fun onPlayPause(isPlaying: Boolean) {
        // No necesario para la lista de favoritos
    }

    override fun onProgressChanged(progress: Int) {
        // No necesario para la lista de favoritos
    }

    private fun updateFavoritesList() {
        favoritesList = favoritesApi.getFavorites().toList()
        favoritesAdapter.updateFavorites(favoritesList)
        // Notificar al adaptador sobre los cambios
        favoritesAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        // Actualizar la interfaz de usuario según el estado actual de la canción
        updateFavoritesList()  // Si la lista de favoritos cambia al volver al fragmento
    }


}
