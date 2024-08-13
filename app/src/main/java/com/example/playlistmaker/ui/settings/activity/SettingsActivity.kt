package com.example.playlistmaker.ui.settings.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.domain.settings.model.DarkThemeState
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel {
        parametersOf(
            application.getSharedPreferences(App.NAME_OF_FILE_WITH_DARK_MODE_CONDITION, MODE_PRIVATE)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backToMainScreenButton = binding.ivBackToMainScreen
        val shareAppButton = binding.tvShareApp
        val writeToSupportButton = binding.tvSupport
        val userAgreementButton = binding.tvUserAgreement
        val themeSwitcher = binding.sNightTheme

        viewModel.getDarkThemeStateLiveData().observe(this) { darkThemeState ->
            when (darkThemeState) {
                is DarkThemeState.DarkTheme -> {
                    themeSwitcher.isChecked =
                        darkThemeState.isDarkThemeOn ?: (applicationContext as App).getDarkModeState()
                }
            }
        }

        viewModel.checkDarkThemeState()

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