package com.example.musicoblaka

import android.os.Bundle
import android.widget.CheckBox
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Settings : AppCompatActivity() {

    private lateinit var darkModeSwitch: Switch
    private lateinit var notificationsCheckbox: CheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

                darkModeSwitch = findViewById(R.id.dark_mode_switch)
                notificationsCheckbox = findViewById(R.id.notifications_checkbox)

                // Set up settings change listeners here
            }
        }

