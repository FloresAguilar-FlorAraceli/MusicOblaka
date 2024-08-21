package com.example.musicoblaka;

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class AnimacionPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.alpha = 1 - Math.abs(position)
        page.translationX = -position * page.width
        page.scaleX = 1 - 0.5f * Math.abs(position)
        page.scaleY = 1 - 0.5f * Math.abs(position)
    }
}
