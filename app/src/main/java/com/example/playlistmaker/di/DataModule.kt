package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.TracksInPlaylistsRepositoryImpl
import com.example.playlistmaker.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.converters.TrackInPlaylistDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
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
import com.example.playlistmaker.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.db.TracksInPlaylistsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
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

    factory<SharPrefRepository> {
        SharPrefRepositoryImpl(get { parametersOf("search_history") })
    }

    factory<SettingsSharPrefRepository> {
        SettingsSharPrefRepositoryImpl(get { parametersOf("night_theme_on_off") })
    }

    factory<SharedPreferences> { (name: String) ->
        androidContext().getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    factory {
        MediaPlayer()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    factory { TrackDbConvertor() }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    factory { PlaylistDbConvertor() }

    single<PlaylistsRepository> {
        PlaylistsRepositoryImpl(get(), get())
    }

    single<TracksInPlaylistsRepository> {
        TracksInPlaylistsRepositoryImpl(get(), get())
    }

    factory { TrackInPlaylistDbConvertor() }

}