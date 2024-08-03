package com.example.playlistmaker.ui.settings.view_model

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.DarkThemeState

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val darkThemeStateLiveData = MutableLiveData<DarkThemeState>()

    fun getDarkThemeStateLiveData() : LiveData<DarkThemeState> = darkThemeStateLiveData

    companion object {
        fun getViewModelFactory(
            sharPref: SharedPreferences
        ) : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    Creator.provideSettingsInteractor(sharPref)
                )
            }
        }
    }

    fun checkDarkThemeState() {
        darkThemeStateLiveData.postValue(DarkThemeState.DarkTheme(settingsInteractor.getDarkThemeStateFromSharPref()))
    }
}