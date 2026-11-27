package com.practicum.playlist_maker.search.data.network

import com.practicum.playlist_maker.search.data.network.NetworkClient
import com.practicum.playlist_maker.search.data.dto.Response
import com.practicum.playlist_maker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class RetrofitNetworkClient(private val itunesApi: ItunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            return try {
                val resp = itunesApi.searchTracks(dto.expression).execute()
                resp.body()?.apply { resultCode = resp.code() }
                    ?: Response().apply { resultCode = resp.code() }
            } catch (e: IOException) {
                Response().apply { resultCode = -1 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

    private companion object {
        private const val BASE_URL = "https://itunes.apple.com"
    }
}