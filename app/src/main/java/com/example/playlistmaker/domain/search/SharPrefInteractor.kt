package com.example.playlistmaker.domain.search

import android.app.Application
import com.example.playlistmaker.domain.search.model.Track

interface SharPrefInteractor {
    val app: Application
    val nameOfFile: String
    val key: String

    fun getResFromSharPref(): Any
    fun putResToSharPref(trackForSave: Track)
    fun removeAllRes()
}