package com.example.playlistmaker.ui.search.activity

import com.example.playlistmaker.domain.search.SharPrefInteractor
import com.example.playlistmaker.domain.search.model.Track

class SearchHistory(private val sharPrefInteractor: SharPrefInteractor) {

    var tracksInSearchHistory: ArrayList<Track> = arrayListOf()

    companion object {
        const val MAX_COUNT_OF_TRACKS_IN_SEARCH_HISTORY = 10
    }

    init {
        getTracksFromSharPref()
    }

    fun saveNewTrack(track: Track) {
        getTracksFromSharPref()

        tracksInSearchHistory.removeIf { it.trackId == track.trackId }

        if (tracksInSearchHistory.size == MAX_COUNT_OF_TRACKS_IN_SEARCH_HISTORY) {
            tracksInSearchHistory.removeLast()
        }

        tracksInSearchHistory.add(0, track)
        sharPrefInteractor.putResToSharPref(tracksInSearchHistory)
    }

    private fun getTracksFromSharPref() {
        tracksInSearchHistory.clear()
        tracksInSearchHistory.addAll(sharPrefInteractor.getResFromSharPref())
    }

    fun removeAllTracks() {
        tracksInSearchHistory.clear()
        sharPrefInteractor.removeAllRes()
    }

}