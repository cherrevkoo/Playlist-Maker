package com.practicum.playlist_maker.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlist_maker.domain.api.SearchHistoryRepository
import com.practicum.playlist_maker.domain.models.Track

class SearchHistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : SearchHistoryRepository {
    private val gson = Gson()
    private val key = "search_history"

    override fun getHistory() : List<Track> {
        val json = sharedPreferences.getString(key, null)
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            gson.fromJson(json, type)
        }
    }

    override fun addTrack(track: Track) {
        val currentList = getHistory().toMutableList()
        currentList.removeAll { it.trackId == track.trackId }
        currentList.add(0, track)

        if (currentList.size > 10) {
            currentList.removeAt(currentList.lastIndex)
        }

        val json = gson.toJson(currentList)
        sharedPreferences.edit().putString(key, json).apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(key).apply()

    }
}