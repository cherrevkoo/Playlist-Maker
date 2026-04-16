package com.practicum.playlist_maker

data class SearchResponse(
    val resultCount: Int,
    val results: List<Song>
)