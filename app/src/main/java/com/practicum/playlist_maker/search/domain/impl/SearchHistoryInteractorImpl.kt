package com.practicum.playlist_maker.search.domain.impl

import com.practicum.playlist_maker.search.domain.model.Track
import com.practicum.playlist_maker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.search.domain.api.SearchHistoryRepository

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