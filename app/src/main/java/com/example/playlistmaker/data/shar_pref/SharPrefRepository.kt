package com.example.playlistmaker.data.shar_pref

import android.app.Application

interface SharPrefRepository {
    val app: Application
    val nameOfFile: String
    val key: String

    fun <T> getArrayListFromResource(): ArrayList<T>
    fun <T> putArrayListInSharPref(res: ArrayList<T>)
    fun removeAllResources()
    fun registerChangeListener(listener: () -> Unit)
    fun unregisterChangeListener()
}