package com.practicum.playlist_maker.creator

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlist_maker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlist_maker.settings.domain.SettingsRepository

class App : Application() {

    private lateinit var sharedPrefs: SharedPreferences

    val DARK_THEME_ENABLED = "DARK_THEME_ENABLED"
    val PLAYLIST_MAKER_SETTINGS = "playlist_maker_settings"


    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val repository = SettingsRepositoryImpl(getSharedPreferences( "playlist_maker_settings",
            MODE_PRIVATE
        ))

        val darkTheme = repository.getThemeSettings().darkTheme

        AppCompatDelegate.setDefaultNightMode(

            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}