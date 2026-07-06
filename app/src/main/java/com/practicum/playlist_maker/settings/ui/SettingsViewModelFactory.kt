package com.practicum.playlist_maker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlist_maker.settings.domain.SettingsInteractor
import com.practicum.playlist_maker.sharing.domain.SharingInteractor

class SettingsViewModelFactory(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(
            sharingInteractor,
            settingsInteractor
        ) as T
    }
}