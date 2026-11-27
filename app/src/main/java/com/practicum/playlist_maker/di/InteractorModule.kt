package com.practicum.playlist_maker.di

import com.practicum.playlist_maker.player.domain.api.PlayerInteractor
import com.practicum.playlist_maker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlist_maker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.search.domain.api.TracksInteractor
import com.practicum.playlist_maker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlist_maker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlist_maker.settings.domain.SettingsInteractor
import com.practicum.playlist_maker.settings.domain.SettingsInteractorImpl
import com.practicum.playlist_maker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlist_maker.sharing.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }
}