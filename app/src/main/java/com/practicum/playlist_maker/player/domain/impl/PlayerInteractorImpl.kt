package com.practicum.playlist_maker.player.domain.impl

import com.practicum.playlist_maker.player.domain.api.PlayerInteractor
import com.practicum.playlist_maker.player.domain.api.PlayerRepository
import com.practicum.playlist_maker.search.domain.model.Track

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getTrack(): Track? {
        return repository.getTrack()
    }
}
