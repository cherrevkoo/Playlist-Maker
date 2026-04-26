package com.practicum.playlist_maker

class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String = "",
    val country: String = ""
) {
    fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

}