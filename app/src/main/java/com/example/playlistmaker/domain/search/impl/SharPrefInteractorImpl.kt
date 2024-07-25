package com.example.playlistmaker.domain.search.impl

import android.app.Application
import com.example.playlistmaker.data.shar_pref.SharPrefRepository
import com.example.playlistmaker.domain.search.SharPrefInteractor
import com.example.playlistmaker.domain.search.model.Track

class SharPrefInteractorImpl(
    override val app: Application,
    override val nameOfFile: String,
    override val key: String,
    private val sharPrefRepository: SharPrefRepository
) : SharPrefInteractor {
    override fun getResFromSharPref(): Any =
        sharPrefRepository.getResource()

    override fun putResToSharPref(trackForSave: Track) =
        sharPrefRepository.putResource(trackForSave)

    override fun removeAllRes() =
        sharPrefRepository.removeAllResources()
}