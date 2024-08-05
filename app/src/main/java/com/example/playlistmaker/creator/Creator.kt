package com.example.playlistmaker.creator

import android.content.SharedPreferences
import com.example.playlistmaker.data.mediaplayer.PlayerForMusic
import com.example.playlistmaker.data.mediaplayer.PlayerRepository
import com.example.playlistmaker.data.mediaplayer.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.data.search.SharPrefRepository
import com.example.playlistmaker.data.search.impl.SharPrefRepositoryImpl
import com.example.playlistmaker.data.settings.SettingsSharPrefRepository
import com.example.playlistmaker.data.settings.impl.SettingsSharPrefRepositoryImpl
import com.example.playlistmaker.domain.player.impl.PlayerInteractor
import com.example.playlistmaker.domain.search.SharPrefInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.SharPrefInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl

object Creator {
    private fun getPlayerRepository(consume: (Int) -> Unit): PlayerRepository =
        PlayerRepositoryImpl(PlayerForMusic(consume))

    fun providePlayerInteractor(consume: (Int) -> Unit): PlayerInteractor =
        PlayerInteractor(getPlayerRepository(consume))

    private fun getTracksRepository(): TracksRepository =
        TracksRepositoryImpl(RetrofitNetworkClient())

    fun provideTracksInteractor(): TracksInteractor =
        TracksInteractorImpl(getTracksRepository())

    private fun getSharPrefRepository(sharPref: SharedPreferences, key: String): SharPrefRepository =
        SharPrefRepositoryImpl(sharPref, key)

    fun provideSharPrefInteractor(sharPref: SharedPreferences, key: String): SharPrefInteractor =
        SharPrefInteractorImpl(getSharPrefRepository(sharPref, key))

    private fun getSettingsSharPrefRepository(sharPref: SharedPreferences): SettingsSharPrefRepository =
        SettingsSharPrefRepositoryImpl(sharPref)

    fun provideSettingsInteractor(sharPref: SharedPreferences): SettingsInteractor =
        SettingsInteractorImpl(getSettingsSharPrefRepository(sharPref))
}