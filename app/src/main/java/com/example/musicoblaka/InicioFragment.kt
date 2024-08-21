package com.example.musicoblaka

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import java.io.IOException

class InicioFragment : Fragment(R.layout.activity_inicio_fragment) {


    private var mediaPlayer: MediaPlayer? = null
    private lateinit var playButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_inicio_fragment, container, false)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }


}
