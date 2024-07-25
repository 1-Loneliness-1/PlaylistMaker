package com.example.playlistmaker.data.sharing

import com.example.playlistmaker.domain.sharing.model.EmailData

interface ExternalNavigator {
    fun shareLink(shareLink: String)
    fun openLink(termsLink: String)
    fun openEmail(emailData: EmailData)
}