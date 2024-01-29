package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backToMainScreenButton = findViewById<ImageView>(R.id.back_arrow)

        backToMainScreenButton.setOnClickListener {
            val mainScreenIntent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(mainScreenIntent)
        }
    }
}