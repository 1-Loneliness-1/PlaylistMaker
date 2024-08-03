package com.example.playlistmaker.domain.settings

interface SettingsInteractor {
    fun getDarkThemeStateFromSharPref(): Boolean?
}