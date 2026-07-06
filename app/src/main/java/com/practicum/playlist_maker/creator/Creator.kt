package com.practicum.playlist_maker.creator

import android.content.Context
import com.practicum.playlist_maker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlist_maker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlist_maker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlist_maker.search.data.network.RetrofitNetworkClient
import com.practicum.playlist_maker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.search.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.search.domain.api.TracksInteractor
import com.practicum.playlist_maker.search.domain.api.TracksRepository
import com.practicum.playlist_maker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlist_maker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlist_maker.settings.domain.SettingsInteractor
import com.practicum.playlist_maker.settings.domain.SettingsInteractorImpl
import com.practicum.playlist_maker.settings.domain.SettingsRepository
import com.practicum.playlist_maker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlist_maker.sharing.data.impl.SharingInteractorImpl
import com.practicum.playlist_maker.sharing.domain.ExternalNavigator
import com.practicum.playlist_maker.sharing.domain.SharingInteractor

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            context.getSharedPreferences(
                "playlist_maker_search_history",
                Context.MODE_PRIVATE
            )
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(
            getSearchHistoryRepository(context)
        )
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            context.getSharedPreferences(
                "playlist_maker_settings",
                Context.MODE_PRIVATE
            )
        )
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(
            getSettingsRepository(context)
        )
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            getExternalNavigator(context)
        )
    }

}