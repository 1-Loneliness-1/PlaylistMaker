package com.example.playlistmaker.data.search

import android.app.Application
import com.example.playlistmaker.domain.search.model.Track

interface SharPrefRepository {
    val app: Application
    val nameOfFile: String
    val key: String

    fun getArrayListFromResource(): ArrayList<Track>
    fun putArrayListInSharPref(res: ArrayList<Track>)
    fun removeAllResources()
    fun registerChangeListener(listener: () -> Unit)
    fun unregisterChangeListener()
}