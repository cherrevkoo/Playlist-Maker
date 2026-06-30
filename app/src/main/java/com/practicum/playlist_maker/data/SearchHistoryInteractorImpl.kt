package com.practicum.playlist_maker.data

import com.practicum.playlist_maker.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}