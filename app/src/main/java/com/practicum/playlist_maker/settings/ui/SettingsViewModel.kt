package com.practicum.playlist_maker.settings.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlist_maker.settings.domain.SettingsInteractor
import com.practicum.playlist_maker.settings.domain.model.ThemeSettings
import com.practicum.playlist_maker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {
    private val stateLiveData = MutableLiveData<SettingsState>()

    init {

        val settings = settingsInteractor.getThemeSettings()

        stateLiveData.value = SettingsState(settings.darkTheme)

    }

    fun observeState(): LiveData<SettingsState> = stateLiveData

    fun onThemeSwitch(checked: Boolean) {
        settingsInteractor.updateThemeSetting(ThemeSettings(checked))
        stateLiveData.value = SettingsState(checked)
    }

    fun onShareClick() {
        sharingInteractor.shareApp()

    }

    fun onSupportClick() {
        sharingInteractor.openSupport()

    }

    fun onTermsClick() {
        sharingInteractor.openTerms()

    }

}