package com.example.playlistmaker.data.settings.impl

import android.content.SharedPreferences
import com.example.playlistmaker.App
import com.example.playlistmaker.data.settings.SettingsSharPrefRepository
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

class SettingsSharPrefRepositoryImpl : SettingsSharPrefRepository {
    override val sharPref: SharedPreferences by getKoin().inject {
        parametersOf(App.NAME_OF_FILE_WITH_DARK_MODE_CONDITION)
    }
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