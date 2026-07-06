package com.practicum.playlist_maker.player.domain.api

import com.practicum.playlist_maker.search.domain.model.Track

interface PlayerInteractor {
    fun saveTrack(track: Track)
    fun getTrack(): Track?
}
