package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var isDarkModeEnabled: Boolean? = null
    private var sharPref: SharedPreferences? = null

    override fun onCreate() {
        super.onCreate()
        isDarkModeEnabled = this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        sharPref = getSharedPreferences("night_theme_on_off", MODE_PRIVATE)
        val sharPrefRes = sharPref?.getString("is_dark_theme", null)
        if (sharPrefRes == null) {
            switchTheme(isDarkModeEnabled!!)
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
        sharPref?.edit()
            ?.putString("is_dark_theme", isDarkThemeEnabled.toString())
            ?.apply()
    }
}