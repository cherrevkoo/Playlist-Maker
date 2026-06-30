package com.practicum.playlist_maker.domain.api

import com.practicum.playlist_maker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
        fun onError() {}
    }
}