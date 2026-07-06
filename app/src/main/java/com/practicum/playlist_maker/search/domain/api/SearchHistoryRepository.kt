package com.practicum.playlist_maker.search.domain.api

import com.practicum.playlist_maker.search.domain.model.Track

interface SearchHistoryRepository {

    fun getHistory(): List<Track>

    fun saveHistory(tracks: List<Track>)

    fun clearHistory()
}
