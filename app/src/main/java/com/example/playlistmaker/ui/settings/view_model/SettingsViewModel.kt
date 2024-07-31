package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.SettingsInteractor

class SettingsViewModel(
    application: Application,
    private val settingsInteractor: SettingsInteractor
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(
            app: Application
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    app,
                    Creator.provideSettingsInteractor(app)
                )
            }
        }
    }

    fun getDarkThemeStateFromSharPref(): Boolean? =
        settingsInteractor.getDarkThemeStateFromSharPref()

    fun saveDarkThemeStateToSharPref(isDarkThemeEnabled: Boolean) =
        settingsInteractor.saveDarkThemeStateToSharPref(isDarkThemeEnabled)

}