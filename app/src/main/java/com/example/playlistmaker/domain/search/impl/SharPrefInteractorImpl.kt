package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.search.SharPrefRepository
import com.example.playlistmaker.domain.search.SharPrefInteractor
import com.example.playlistmaker.domain.search.model.Track

class SharPrefInteractorImpl(
    private val sharPrefRepository: SharPrefRepository
) : SharPrefInteractor {

    override fun getResFromSharPref(): ArrayList<Track> =
        sharPrefRepository.getArrayListFromResource()

    override fun putResToSharPref(tracksInSearchHistory: ArrayList<Track>) =
        sharPrefRepository.putArrayListInSharPref(tracksInSearchHistory)

    override fun removeAllRes() =
        sharPrefRepository.removeAllResources()

    override fun registerChangeListener(listener: () -> Unit) =
        sharPrefRepository.registerChangeListener(listener)


    override fun unregisterChangeListener() =
        sharPrefRepository.unregisterChangeListener()
}