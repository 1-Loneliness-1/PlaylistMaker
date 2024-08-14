package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
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
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dataModule = module {

    factoryOf(::PlayerRepositoryImpl) {
        bind<PlayerRepository>()
    }

    factoryOf(::PlayerForMusic) {
        bind<Playable>()
    }

    factoryOf(::TracksRepositoryImpl) {
        bind<TracksRepository>()
    }

    factoryOf(::RetrofitNetworkClient) {
        bind<NetworkClient>()
    }

    factoryOf(::SharPrefRepositoryImpl) {
        bind<SharPrefRepository>()
    }

    factoryOf(::SettingsSharPrefRepositoryImpl) {
        bind<SettingsSharPrefRepository>()
    }

    factory<SharedPreferences> { (name: String) ->
        androidContext().getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    factory {
        MediaPlayer()
    }
}