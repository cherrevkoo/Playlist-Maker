package com.practicum.playlist_maker.search.data.repository

import com.practicum.playlist_maker.search.data.dto.TrackSearchRequest
import com.practicum.playlist_maker.search.data.dto.TrackSearchResponse
import com.practicum.playlist_maker.search.data.network.NetworkClient
import com.practicum.playlist_maker.search.data.toDomain
import com.practicum.playlist_maker.search.domain.api.TracksRepository
import com.practicum.playlist_maker.search.domain.model.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode != 200) {
            throw IllegalStateException()
        }
        val searchResponse = response as? TrackSearchResponse
            ?: throw IllegalStateException()
        return searchResponse.results.map { it.toDomain() }
    }
}
