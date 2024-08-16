package com.example.playlistmaker.data.settings.impl

import android.content.SharedPreferences
import com.example.playlistmaker.data.settings.SettingsSharPrefRepository

class SettingsSharPrefRepositoryImpl(
    override val sharPref: SharedPreferences
) : SettingsSharPrefRepository {

    companion object {
        const val KEY_OF_DARK_MODE = "is_dark_theme"
    }

    override fun getDarkThemeStateFromSharPref(): Boolean? {
        return if (sharPref.getString(KEY_OF_DARK_MODE, null) == null) {
            null
        } else {
            sharPref.getString(KEY_OF_DARK_MODE, null).toBoolean()
        }
    }
}