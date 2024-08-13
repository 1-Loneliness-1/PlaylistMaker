package com.example.playlistmaker.di

import android.content.SharedPreferences
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.SharPrefInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.SharPrefInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val domainModule = module {
    factory<PlayerInteractor> { (consume: (Int) -> Unit) ->
        PlayerInteractorImpl(
            get {
                parametersOf(consume)
            }
        )
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SharPrefInteractor> { (sharPref: SharedPreferences, key: String) ->
        SharPrefInteractorImpl(
            get {
                parametersOf(sharPref, key)
            }
        )
    }

    single<SettingsInteractor> { (sharPref: SharedPreferences) ->
        SettingsInteractorImpl(
            get {
                parametersOf(sharPref)
            }
        )
    }
}