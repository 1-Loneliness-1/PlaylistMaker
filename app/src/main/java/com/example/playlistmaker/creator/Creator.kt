package com.example.playlistmaker.creator

import com.example.playlistmaker.data.mediaplayer.PlayerForMusic
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.usecases.PlayerInteractor

object Creator {
    private fun getPlayerRepository(urlOfMusic: String): PlayerRepository {
        return PlayerRepositoryImpl(PlayerForMusic(urlOfMusic))
    }

    fun providePlayerInteractor(url: String): PlayerInteractor {
        return PlayerInteractor(getPlayerRepository(url))
    }
}