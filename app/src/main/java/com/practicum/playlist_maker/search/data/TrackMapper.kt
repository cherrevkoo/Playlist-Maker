package com.practicum.playlist_maker.search.data

import com.practicum.playlist_maker.search.data.dto.TrackDTO
import com.practicum.playlist_maker.search.domain.model.Track

fun TrackDTO.toDomain(): Track {
    return Track(
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTimeMillis,
        artworkUrl100 = artworkUrl100,
        trackId = trackId,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}

fun Track.toDto(): TrackDTO {
    return TrackDTO(
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTime,
        artworkUrl100 = artworkUrl100,
        trackId = trackId,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}
