package com.practicum.playlist_maker.search.domain.api

import com.practicum.playlist_maker.search.domain.model.Track

interface SearchHistoryInteractor {

    fun getHistory(): List<Track>

    fun addTrack(track: Track)

    fun clearHistory()
}