package com.example.playlistmaker.data.search

import android.content.SharedPreferences
import com.example.playlistmaker.domain.search.model.Track

interface SharPrefRepository {
    val sharPref: SharedPreferences
    val key: String

    fun getArrayListFromResource(): ArrayList<Track>
    fun putArrayListInSharPref(res: ArrayList<Track>)
    fun removeAllResources()
    fun registerChangeListener(listener: () -> Unit)
    fun unregisterChangeListener()
}