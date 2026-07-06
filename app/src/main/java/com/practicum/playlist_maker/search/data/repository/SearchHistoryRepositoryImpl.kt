package com.practicum.playlist_maker.search.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlist_maker.search.data.dto.TrackDTO
import com.practicum.playlist_maker.search.data.toDomain
import com.practicum.playlist_maker.search.data.toDto
import com.practicum.playlist_maker.search.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.search.domain.model.Track

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SearchHistoryRepository {
    private val gson = Gson()
    private val key = "search_history"

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(key, null)
        if (json.isNullOrEmpty()) {
            return emptyList()
        }
        return try {
            val type = object : TypeToken<ArrayList<TrackDTO>>() {}.type
            val dtoList: List<TrackDTO> = gson.fromJson(json, type) ?: emptyList()
            dtoList.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun saveHistory(tracks: List<Track>) {
        val json = gson.toJson(tracks.map { it.toDto() })
        sharedPreferences.edit().putString(key, json).apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(key).apply()
    }
}
