package com.practicum.playlist_maker.data.network

import com.practicum.playlist_maker.data.NetworkClient
import com.practicum.playlist_maker.data.dto.Response
import com.practicum.playlist_maker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitNetworkClient : NetworkClient {
    private val imdbBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(ItunesApi::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            val resp = imdbService.searchTracks(dto.expression).execute()

            val body = resp.body() ?: Response()

            return body.apply {
                resultCode = resp.code()
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

}