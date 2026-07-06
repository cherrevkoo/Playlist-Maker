package com.practicum.playlist_maker.player.ui

import com.practicum.playlist_maker.search.domain.model.Track
import com.practicum.playlist_maker.search.ui.TrackParcelable

fun TrackParcelable.toDomain(): Track {
    return Track(
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl100 = artworkUrl100,
        trackId = trackId,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country,
        previewUrl = previewUrl
    )
}
