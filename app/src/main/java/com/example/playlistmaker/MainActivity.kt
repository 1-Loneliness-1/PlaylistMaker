package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val mediaButton = findViewById<Button>(R.id.media_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        //Listener implementation for search button
        val searchButtonListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажатие на кнопку поиска!", Toast.LENGTH_LONG).show()
            }
        }

        searchButton.setOnClickListener(searchButtonListener)

        //Listener implementation for media button
        mediaButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажатие на кнопку медитека!", Toast.LENGTH_SHORT).show()
        }

        //Listener implementation for settings button
        val settingsButtonListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажатие на кнопку настроек!", Toast.LENGTH_LONG).show()
            }
        }

        settingsButton.setOnClickListener(settingsButtonListener)
    }
}