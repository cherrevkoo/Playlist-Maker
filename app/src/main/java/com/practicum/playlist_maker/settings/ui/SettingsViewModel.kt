package com.practicum.playlist_maker.settings.ui

import androidx.lifecycle.ViewModel
import com.practicum.playlist_maker.settings.domain.SettingsInteractor
import com.practicum.playlist_maker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {
}