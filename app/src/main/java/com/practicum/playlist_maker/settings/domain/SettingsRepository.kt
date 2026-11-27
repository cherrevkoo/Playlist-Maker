package com.practicum.playlist_maker.settings.domain

import com.practicum.playlist_maker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}