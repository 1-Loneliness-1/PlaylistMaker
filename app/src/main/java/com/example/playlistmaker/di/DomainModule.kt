package com.example.playlistmaker.di

import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
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

}