package com.example.playlistmaker.data.settings

import android.content.SharedPreferences

interface SettingsSharPrefRepository {
    val sharPref: SharedPreferences

    fun getDarkThemeStateFromSharPref(): Boolean?
}