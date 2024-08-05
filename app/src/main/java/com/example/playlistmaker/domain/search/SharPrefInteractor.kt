package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface SharPrefInteractor {
    fun getResFromSharPref(): ArrayList<Track>
    fun putResToSharPref(tracksInSearchHistory: ArrayList<Track>)
    fun removeAllRes()
    fun registerChangeListener(listener: () -> Unit)
    fun unregisterChangeListener()
}