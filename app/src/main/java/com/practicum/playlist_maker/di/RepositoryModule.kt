package com.practicum.playlist_maker.di

import com.practicum.playlist_maker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlist_maker.player.domain.api.PlayerRepository
import com.practicum.playlist_maker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlist_maker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlist_maker.search.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.search.domain.api.TracksRepository
import com.practicum.playlist_maker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlist_maker.settings.domain.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(
            named("search_history")),
            get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named("settings"))
        )
    }

    single<PlayerRepository> {
        PlayerRepositoryImpl(get(
            named("player_state")),
            get()
        )
    }
}
