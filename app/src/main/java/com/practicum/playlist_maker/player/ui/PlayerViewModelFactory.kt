package com.practicum.playlist_maker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlist_maker.player.domain.api.PlayerInteractor

class PlayerViewModelFactory(
    private val playerInteractor: PlayerInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(playerInteractor) as T
    }
}
