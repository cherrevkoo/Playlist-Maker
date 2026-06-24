package com.practicum.playlist_maker.domain.api

import com.practicum.playlist_maker.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}