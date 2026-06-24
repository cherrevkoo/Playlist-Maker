package com.practicum.playlist_maker.domain.api
import com.practicum.playlist_maker.domain.models.Track

interface SearchHistoryRepository {

    fun getHistory(): List<Track>

    fun addTrack(track: Track)

    fun clearHistory()
}