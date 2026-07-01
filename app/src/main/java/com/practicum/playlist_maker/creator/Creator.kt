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