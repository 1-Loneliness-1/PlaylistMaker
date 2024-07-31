package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class App : Application() {

    private var sharPref: SharedPreferences? = null

    companion object {
        const val NAME_OF_FILE_WITH_DARK_MODE_CONDITION = "night_theme_on_off"
        const val KEY_OF_DARK_MODE = "is_dark_theme"
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }

    override fun onCreate() {
        super.onCreate()
        val isDarkModeEnabled = this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        sharPref = getSharedPreferences(NAME_OF_FILE_WITH_DARK_MODE_CONDITION, MODE_PRIVATE)
        val sharPrefRes = sharPref?.getString(KEY_OF_DARK_MODE, null)
        if (sharPrefRes == null) {
            switchTheme(isDarkModeEnabled)
        } else {
            switchTheme(sharPrefRes.toBoolean())
        }
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharPref?.edit {
            putString("is_dark_theme", isDarkThemeEnabled.toString())
        }
    }

    fun getDarkModeState() = this.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

}