package com.example.musicoblaka

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InicioFragment()
            1 -> ListaReproduccionFragment()
            2 -> FavoritosFragment()
            3 -> ArtistaFragment()
            else -> InicioFragment()
        }
    }
}
