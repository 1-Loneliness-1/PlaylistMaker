package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backToMainScreenButton = findViewById<ImageView>(R.id.back_to_main_screen_imageview)
        val shareAppButton = findViewById<TextView>(R.id.share_app_textview)
        val writeToSupportButton = findViewById<TextView>(R.id.support_textview)
        val userAgreementButton = findViewById<TextView>(R.id.user_agreement_textview)

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