package com.practicum.playlist_maker.player.ui

data class PlayerState(
    val trackName: String = "",
    val artistName: String = "",
    val albumName: String = "",
    val releaseYear: String = "",
    val genre: String = "",
    val country: String = "",
    val duration: String = "",
    val artworkUrl: String = "",
    val isPlaying: Boolean = false,
    val playTime: String = "00:00",
    val isLiked: Boolean = false,
)
