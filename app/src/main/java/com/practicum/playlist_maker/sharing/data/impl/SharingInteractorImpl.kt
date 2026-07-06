package com.practicum.playlist_maker.sharing.data.impl

import com.practicum.playlist_maker.sharing.domain.ExternalNavigator
import com.practicum.playlist_maker.sharing.domain.SharingInteractor
import com.practicum.playlist_maker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp() = externalNavigator.shareApp()

    override fun openTerms() = externalNavigator.openTerms()

    override fun openSupport() = externalNavigator.openSupport()
}
