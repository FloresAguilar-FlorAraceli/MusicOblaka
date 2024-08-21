package com.example.musicoblaka

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class FavoritesApi(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)

    fun getFavorites(): List<String> {
        val favoritesSet = sharedPreferences.getStringSet("favorites", emptySet()) ?: emptySet()
        return favoritesSet.toList()
    }

    fun isFavorite(songPath: String): Boolean {
        val favoritesSet = sharedPreferences.getStringSet("favorites", emptySet()) ?: emptySet()
        return favoritesSet.contains(songPath)
    }

    fun addFavorite(songPath: String) {
        try {
            val favoritesSet = sharedPreferences.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()
            favoritesSet.add(songPath)
            with(sharedPreferences.edit()) {
                putStringSet("favorites", favoritesSet)
                apply()
            }
        } catch (e: Exception) {
            Log.e("FavoritesApi", "Error al agregar Favoritos", e)
        }
    }

    fun removeFavorite(songPath: String) {
        try {
            val favoritesSet = sharedPreferences.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()
            favoritesSet.remove(songPath)
            with(sharedPreferences.edit()) {
                putStringSet("favorites", favoritesSet)
                apply()
            }
        } catch (e: Exception) {
            Log.e("FavoritesApi", "Error al eliminar de Favoritos", e)
        }
    }

}
