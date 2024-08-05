package com.example.playlistmaker.domain.settings.model

sealed class DarkThemeState {
    data class DarkTheme(
        val isDarkThemeOn: Boolean?
    ): DarkThemeState()
}