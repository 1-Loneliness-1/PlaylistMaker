package com.example.playlistmaker.domain.settings

interface SettingsInteractor {
    fun getDarkThemeStateFromSharPref(): Boolean?
    fun saveDarkThemeStateToSharPref(isDarkThemeEnabled: Boolean)
}