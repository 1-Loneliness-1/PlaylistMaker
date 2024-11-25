package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.DarkThemeState

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val darkThemeStateLiveData = MutableLiveData<DarkThemeState>()

    fun getDarkThemeStateLiveData() : LiveData<DarkThemeState> = darkThemeStateLiveData

    fun checkDarkThemeState() {
        darkThemeStateLiveData.postValue(DarkThemeState(settingsInteractor.getDarkThemeStateFromSharPref()))
    }

}