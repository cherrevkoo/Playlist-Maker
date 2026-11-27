package com.practicum.playlist_maker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.search.domain.api.TracksInteractor
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private var searchQuery: String = ""
    private var searchJob: Job? = null
    private val stateLiveData = MutableLiveData<SearchState>()

    init {
        showHistory()
    }

    fun observeState(): LiveData<SearchState> = stateLiveData

    fun onSearchTextChanged(query: String, hasFocus: Boolean) {
        searchQuery = query
        searchJob?.cancel()
        when {
            query.isBlank() -> {
                if (hasFocus) {
                    showHistory()
                } else {
                    renderState(SearchState.Idle)
                }
            }
            else -> {
                searchJob = viewModelScope.launch {
                    delay(SEARCH_DEBOUNCE_DELAY)
                    performSearch(query)
                }
            }
        }
    }

    fun onFocusChanged(hasFocus: Boolean) {
        if (searchQuery.isBlank()) {
            if (hasFocus) {
                showHistory()
            } else {
                renderState(SearchState.Idle)
            }
        }
    }

    fun addTrack(track: Track) {
        searchHistoryInteractor.addTrack(track)
        showHistory()
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        showHistory()
    }

    fun retrySearch() {
        if (searchQuery.isNotBlank()) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                performSearch(searchQuery)
            }
        }
    }

    private suspend fun performSearch(query: String) {
        searchQuery = query
        renderState(SearchState.Loading)
        try {
            val foundTracks = withContext(Dispatchers.IO) {
                tracksInteractor.searchTracks(query)
            }
            if (foundTracks.isEmpty()) {
                renderState(SearchState.Empty)
            } else {
                renderState(SearchState.Content(foundTracks))
            }
        } catch (e: Exception) {
            renderState(SearchState.Error)
        }
    }

    private fun showHistory() {
        renderState(SearchState.History(searchHistoryInteractor.getHistory()))
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
