package com.practicum.playlist_maker.search.ui

import android.os.Parcelable
import com.practicum.playlist_maker.search.domain.model.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackParcelable(
    val trackName: String?,
    val artistName: String?,
    val trackTime: Long?,
    val artworkUrl100: String?,
    val trackId: Long,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = "",
    val country: String? = "",
    val previewUrl: String? = ""
) : Parcelable

fun Track.toParcelable(): TrackParcelable {
    return TrackParcelable(
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
