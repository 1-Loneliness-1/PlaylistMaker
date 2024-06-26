package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backToMainScreenButton = findViewById<ImageView>(R.id.ivBackToMainScreen)
        val shareAppButton = findViewById<TextView>(R.id.tvShareApp)
        val writeToSupportButton = findViewById<TextView>(R.id.tvSupport)
        val userAgreementButton = findViewById<TextView>(R.id.tvUserAgreement)
        val themeSwitcher = findViewById<Switch>(R.id.sNightTheme)
        val darkThemeSharPref =
            getSharedPreferences(App.NAME_OF_FILE_WITH_DARK_MODE_CONDITION, MODE_PRIVATE)

        themeSwitcher.isChecked =
            darkThemeSharPref.getString(App.KEY_OF_DARK_MODE, null).toBoolean()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        backToMainScreenButton.setOnClickListener { finish() }

        shareAppButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.yp_android_course_link))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_apk_text)))
        }

        writeToSupportButton.setOnClickListener {
            val writeToSupportIntent = Intent(Intent.ACTION_SEND)
            writeToSupportIntent.setType("message/rfc822")
                .putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)) )
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_for_android_developers))
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.acknowledge_for_android_dev))
            startActivity(Intent.createChooser(writeToSupportIntent, getString(R.string.write_to_support)))
        }

        userAgreementButton.setOnClickListener {
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.yp_offertory_link)))
            startActivity(userAgreementIntent)
        }
    }
}