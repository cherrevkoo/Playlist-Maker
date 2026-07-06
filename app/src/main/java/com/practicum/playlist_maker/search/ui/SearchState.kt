package com.practicum.playlist_maker.search.ui

import com.practicum.playlist_maker.search.domain.model.Track

sealed class SearchState {
    object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
    object Idle : SearchState()
}
