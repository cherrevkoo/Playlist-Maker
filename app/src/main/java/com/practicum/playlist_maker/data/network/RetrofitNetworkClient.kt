package com.practicum.playlist_maker.data.network

import com.practicum.playlist_maker.data.NetworkClient
import com.practicum.playlist_maker.data.dto.Response
import com.practicum.playlist_maker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class RetrofitNetworkClient : NetworkClient {
    private val imdbBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(ItunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            return try {
                val resp = imdbService.searchTracks(dto.expression).execute()
                resp.body()?.apply { resultCode = resp.code() }
                    ?: Response().apply { resultCode = resp.code() }
            } catch (e: IOException) {
                Response().apply { resultCode = -1 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

}