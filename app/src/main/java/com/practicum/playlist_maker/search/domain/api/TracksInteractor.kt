package com.practicum.playlist_maker.search.domain.api

import com.practicum.playlist_maker.search.domain.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String): List<Track>
}
