package com.practicum.playlist_maker.domain.impl


import java.util.concurrent.Executors
import com.practicum.playlist_maker.domain.api.TracksInteractor
import com.practicum.playlist_maker.domain.api.TracksRepository

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TrackConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(expression))
        }
    }

}