package com.example.playlistmaker.di

import com.example.playlistmaker.ui.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.media.view_model.MediaViewModel
import com.example.playlistmaker.ui.media.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.media.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.playlist.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {

    viewModelOf(::PlayerViewModel)

    viewModelOf(::SearchViewModel)

    viewModelOf(::SettingsViewModel)

    viewModelOf(::MediaViewModel)

    viewModelOf(::FavoriteTracksViewModel)

    viewModelOf(::PlaylistsViewModel)

    viewModelOf(::NewPlaylistViewModel)

    viewModelOf(::PlaylistInfoViewModel)

}