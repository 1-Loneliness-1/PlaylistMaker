package com.example.playlistmaker.data.shar_pref

import android.app.Application

interface SharPrefRepository {
    val app: Application
    val nameOfFile: String
    val key: String

    fun getResource(): Any
    fun putResource(res: Any)
    fun removeAllResources()
}