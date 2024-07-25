package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.data.mediaplayer.PlayerForMusic
import com.example.playlistmaker.data.mediaplayer.PlayerRepository
import com.example.playlistmaker.data.mediaplayer.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.data.shar_pref.SharPrefRepository
import com.example.playlistmaker.data.shar_pref.impl.SharPrefRepositoryImpl
import com.example.playlistmaker.domain.player.impl.PlayerInteractor
import com.example.playlistmaker.domain.search.SharPrefInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.SharPrefInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl

object Creator {
    private fun getPlayerRepository(urlOfMusic: String): PlayerRepository {
        return PlayerRepositoryImpl(PlayerForMusic(urlOfMusic))
    }

    fun providePlayerInteractor(url: String): PlayerInteractor {
        return PlayerInteractor(getPlayerRepository(url))
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSharPrefRepository(): SharPrefRepository =
        SharPrefRepositoryImpl()

    fun provideSharPrefInteractor(app: Application): SharPrefInteractor =
        SharPrefInteractorImpl(app, getSharPrefRepository())
}