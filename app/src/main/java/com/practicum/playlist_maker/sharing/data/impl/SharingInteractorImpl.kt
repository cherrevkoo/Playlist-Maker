package com.practicum.playlist_maker.sharing.data.impl

import com.practicum.playlist_maker.sharing.domain.ExternalNavigator
import com.practicum.playlist_maker.sharing.domain.SharingInteractor

//class SharingInteractorImpl(
//    private val externalNavigator: ExternalNavigator,
//) : SharingInteractor {
//    override fun shareApp() {
//        externalNavigator.shareLink(getShareAppLink())
//    }
//
//    override fun openTerms() {
//        externalNavigator.openLink(getTermsLink())
//    }
//
//    override fun openSupport() {
//        externalNavigator.openEmail(getSupportEmailData())
//    }
//
//    private fun getShareAppLink(): String {
//        // Нужно реализовать
//    }
//
//    private fun getSupportEmailData(): EmailData {
//        // Нужно реализовать
//    }
//
//    private fun getTermsLink(): String {
//        // Нужно реализовать
//    }
//}