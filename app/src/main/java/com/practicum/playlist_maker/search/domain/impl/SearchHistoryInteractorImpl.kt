package com.practicum.playlist_maker.search.domain.impl

import com.practicum.playlist_maker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.search.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.search.domain.model.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) {
        val currentList = repository.getHistory().toMutableList()
        currentList.removeAll { it.trackId == track.trackId }
        currentList.add(0, track)
        if (currentList.size > 10) {
            currentList.removeAt(currentList.lastIndex)
        }
        repository.saveHistory(currentList)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}
