package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.playlistmaker.databinding.ActivityMediaBinding

class MediaActivity : ComponentActivity() {
    private lateinit var binding: ActivityMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}