package com.practicum.playlist_maker.search.domain.impl

import com.practicum.playlist_maker.search.domain.api.TracksInteractor
import com.practicum.playlist_maker.search.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TrackConsumer) {
        executor.execute {
            try {
                consumer.consume(repository.searchTracks(expression))
            } catch (e: Exception) {
                consumer.onError()
            }
        }
    }

}