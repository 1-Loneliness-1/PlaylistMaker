package com.example.playlistmaker.di

import android.content.SharedPreferences
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val presentationModule = module {
    viewModel { (consume: (Int) -> Unit) ->
        PlayerViewModel(
            get {
                parametersOf(consume)
            }
        )
    }

    viewModel { (sharPref: SharedPreferences, key: String) ->
        SearchViewModel(
            get(),
            get { parametersOf(sharPref, key) }
        )
    }

    viewModel { (sharPref: SharedPreferences) ->
        SettingsViewModel(
            get {
                parametersOf(sharPref)
            }
        )
    }
}