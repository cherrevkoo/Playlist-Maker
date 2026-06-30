package com.practicum.playlist_maker

import android.content.Context
import com.practicum.playlist_maker.data.SearchHistoryInteractorImpl
import com.practicum.playlist_maker.data.SearchHistoryRepositoryImpl
import com.practicum.playlist_maker.data.TracksRepositoryImpl
import com.practicum.playlist_maker.data.network.RetrofitNetworkClient
import com.practicum.playlist_maker.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.domain.api.TracksInteractor
import com.practicum.playlist_maker.domain.api.TracksRepository
import com.practicum.playlist_maker.domain.impl.TracksInteractorImpl

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
}