package com.example.playlistmaker.ui.main.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.media.activity.MediaActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.ui.settings.activity.SettingsActivity

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Listener implementation for search button
        binding.bSearch.setOnClickListener {
            val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        //Listener implementation for media button
        binding.bMedia.setOnClickListener {
            val mediaIntent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        //Listener implementation for settings button
        binding.bSettings.setOnClickListener {
            val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}