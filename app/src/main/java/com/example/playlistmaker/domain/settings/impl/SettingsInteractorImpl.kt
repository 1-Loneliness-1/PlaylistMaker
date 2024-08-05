package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.data.settings.SettingsSharPrefRepository
import com.example.playlistmaker.domain.settings.SettingsInteractor

class SettingsInteractorImpl(
    private val settingsSharPrefRepository: SettingsSharPrefRepository
) : SettingsInteractor {
    override fun getDarkThemeStateFromSharPref(): Boolean? =
        settingsSharPrefRepository.getDarkThemeStateFromSharPref()
}