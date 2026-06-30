package com.practicum.playlist_maker.data

import com.practicum.playlist_maker.data.dto.TrackSearchRequest
import com.practicum.playlist_maker.data.dto.TrackSearchResponse
import com.practicum.playlist_maker.domain.api.TracksRepository
import com.practicum.playlist_maker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode != 200) {
            throw IllegalStateException()
        }
        val searchResponse = response as? TrackSearchResponse
            ?: throw IllegalStateException()
        return searchResponse.results.map {
            Track(
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.trackId,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }
}
