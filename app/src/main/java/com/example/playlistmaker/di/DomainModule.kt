package com.example.playlistmaker.di

import com.example.playlistmaker.domain.media.FavoriteTracksListInteractor
import com.example.playlistmaker.domain.media.PlaylistsInteractor
import com.example.playlistmaker.domain.media.impl.FavoriteTracksListInteractorImpl
import com.example.playlistmaker.domain.media.impl.PlaylistsInteractorImpl
import com.example.playlistmaker.domain.player.BottomSheetPlaylistsInteractor
import com.example.playlistmaker.domain.player.FavoriteTracksInteractor
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.BottomSheetPlaylistsInteractorImpl
import com.example.playlistmaker.domain.player.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.playlist.TracksInPlaylistInteractor
import com.example.playlistmaker.domain.playlist.impl.TracksInPlaylistInteractorImpl
import com.example.playlistmaker.domain.search.SharPrefInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.SharPrefInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {

    factoryOf(::PlayerInteractorImpl) {
        bind<PlayerInteractor>()
    }

    factoryOf(::TracksInteractorImpl) {
        bind<TracksInteractor>()
    }

    factoryOf(::SharPrefInteractorImpl) {
        bind<SharPrefInteractor>()
    }

    factoryOf(::SettingsInteractorImpl) {
        bind<SettingsInteractor>()
    }

    single<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    single<FavoriteTracksListInteractor> {
        FavoriteTracksListInteractorImpl(get())
    }

    single<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }

    single<BottomSheetPlaylistsInteractor> {
        BottomSheetPlaylistsInteractorImpl(get(), get())
    }

    single<TracksInPlaylistInteractor> {
        TracksInPlaylistInteractorImpl(get(), get())
    }

}