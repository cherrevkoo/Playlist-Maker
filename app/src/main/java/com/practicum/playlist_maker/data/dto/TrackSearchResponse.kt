package com.practicum.playlist_maker.data.dto

data class TrackSearchResponse(
    val resultCount: Int,
    val results: List<TrackDTO>
) : Response()