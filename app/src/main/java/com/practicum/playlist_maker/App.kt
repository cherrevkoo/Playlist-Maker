package com.practicum.playlist_maker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private lateinit var sharedPrefs: SharedPreferences

    val DARK_THEME_ENABLED = "DARK_THEME_ENABLED"
    val PLAYLIST_MAKER_SETTINGS = "playlist_maker_settings"


    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_SETTINGS, MODE_PRIVATE)

        val darkThemeEnabled = sharedPrefs.getBoolean(DARK_THEME_ENABLED, false)

        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        sharedPrefs.edit()
            .putBoolean(DARK_THEME_ENABLED, darkThemeEnabled)
            .apply()
        }
    }