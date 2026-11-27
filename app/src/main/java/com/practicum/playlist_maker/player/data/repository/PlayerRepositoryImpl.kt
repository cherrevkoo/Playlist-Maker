package com.practicum.playlist_maker.player.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlist_maker.player.domain.api.PlayerRepository
import com.practicum.playlist_maker.search.data.dto.TrackDTO
import com.practicum.playlist_maker.search.data.toDomain
import com.practicum.playlist_maker.search.data.toDto
import com.practicum.playlist_maker.search.domain.model.Track

class PlayerRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : PlayerRepository {

    override fun saveTrack(track: Track) {
        sharedPreferences.edit()
            .putString(TRACK_KEY, gson.toJson(track.toDto()))
            .apply()
    }

    override fun getTrack(): Track? {
        val json = sharedPreferences.getString(TRACK_KEY, null) ?: return null
        return try {
            gson.fromJson(json, TrackDTO::class.java).toDomain()
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        private const val TRACK_KEY = "player_track"
    }
}
