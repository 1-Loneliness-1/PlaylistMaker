package com.example.playlistmaker.data.settings.impl

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.example.playlistmaker.data.settings.SettingsSharPrefRepository

class SettingsSharPrefRepositoryImpl(
    override val app: Application
) : SettingsSharPrefRepository {

    companion object {
        const val NAME_OF_FILE_WITH_DARK_MODE_CONDITION = "night_theme_on_off"
        const val KEY_OF_DARK_MODE = "is_dark_theme"
    }

    private val sharPref =
        app.getSharedPreferences(NAME_OF_FILE_WITH_DARK_MODE_CONDITION, MODE_PRIVATE)

    override fun getDarkThemeStateFromSharPref(): Boolean? {
        if (sharPref.getString(KEY_OF_DARK_MODE, null) == null) {
            return null
        } else {
            return sharPref.getString(KEY_OF_DARK_MODE, null).toBoolean()
        }
    }

    override fun saveDarkThemeState(isDarkThemeEnabled: Boolean) {
        sharPref.edit {
            putString(KEY_OF_DARK_MODE, isDarkThemeEnabled.toString())
        }
    }
}