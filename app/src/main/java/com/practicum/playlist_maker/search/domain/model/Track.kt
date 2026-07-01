package com.practicum.playlist_maker.search.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
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
) : Parcelable {

    fun getCoverArtwork(): String {
        return artworkUrl100!!.replaceAfterLast('/', "512x512bb.jpg")
    }

}