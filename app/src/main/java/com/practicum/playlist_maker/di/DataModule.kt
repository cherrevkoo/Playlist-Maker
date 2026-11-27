package com.practicum.playlist_maker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlist_maker.search.data.network.ItunesApi
import com.practicum.playlist_maker.search.data.network.NetworkClient
import com.practicum.playlist_maker.search.data.network.RetrofitNetworkClient
import com.practicum.playlist_maker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlist_maker.sharing.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<SharedPreferences>(named("search_history")) {
        androidContext().getSharedPreferences(
            "playlist_maker_search_history",
            Context.MODE_PRIVATE
        )
    }

    single<SharedPreferences>(named("settings")) {
        androidContext().getSharedPreferences(
            "playlist_maker_settings",
            Context.MODE_PRIVATE
        )
    }

    single<SharedPreferences>(named("player_state")) {
        androidContext().getSharedPreferences(
            "player_state",
            Context.MODE_PRIVATE
        )
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory { Gson() }

    factory { MediaPlayer() }
}

