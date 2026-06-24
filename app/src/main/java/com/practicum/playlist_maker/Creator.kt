package com.practicum.playlist_maker

import com.practicum.playlist_maker.data.TracksRepositoryImpl
import com.practicum.playlist_maker.data.network.RetrofitNetworkClient
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
}