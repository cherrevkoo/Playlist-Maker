package com.practicum.playlist_maker.settings.data.impl

import android.content.SharedPreferences
import com.practicum.playlist_maker.settings.domain.SettingsRepository
import com.practicum.playlist_maker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SettingsRepository {
    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }

    override fun getThemeSettings(): ThemeSettings {
        val darkTheme = sharedPreferences.getBoolean(DARK_THEME_KEY, false)
        return ThemeSettings(darkTheme)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, settings.darkTheme)
            .apply()
    }
}