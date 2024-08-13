package com.example.playlistmaker.di

import android.content.SharedPreferences
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.mediaplayer.Playable
import com.example.playlistmaker.data.mediaplayer.PlayerForMusic
import com.example.playlistmaker.data.mediaplayer.PlayerRepository
import com.example.playlistmaker.data.mediaplayer.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.SharPrefRepository
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.data.search.impl.SharPrefRepositoryImpl
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsSharPrefRepository
import com.example.playlistmaker.data.settings.impl.SettingsSharPrefRepositoryImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val dataModule = module {
    factory<PlayerRepository> { (consume: (Int) -> Unit) ->
        PlayerRepositoryImpl(
            get {
                parametersOf(consume)
            }
        )
    }

    factory<Playable> { (consume: (Int) -> Unit) ->
        PlayerForMusic(consume)
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient()
    }

    single<SharPrefRepository> { (sharPref: SharedPreferences, key: String) ->
        SharPrefRepositoryImpl(sharPref, key)
    }

    single<SettingsSharPrefRepository> { (sharPref: SharedPreferences) ->
        SettingsSharPrefRepositoryImpl(sharPref)
    }
}