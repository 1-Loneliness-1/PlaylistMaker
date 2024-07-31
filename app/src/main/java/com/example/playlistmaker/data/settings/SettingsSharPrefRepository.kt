package com.example.playlistmaker.data.settings

import android.app.Application

interface SettingsSharPrefRepository {
    val app: Application

    fun getDarkThemeStateFromSharPref(): Boolean?
    fun saveDarkThemeState(isDarkThemeEnabled: Boolean)
}