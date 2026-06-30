package com.practicum.playlist_maker.data

import com.practicum.playlist_maker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}