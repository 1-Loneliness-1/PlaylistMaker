package com.example.playlistmaker.presentation.search

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.entities.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharPref: SharedPreferences) {

    var tracksInSearchHistory: ArrayList<Track> = arrayListOf()

    companion object {
        const val KEY_FOR_ARRAY_WITH_SEARCH_HISTORY = "elems_in_search_history"
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
        sharPref.edit {
            putString(KEY_FOR_ARRAY_WITH_SEARCH_HISTORY, Gson().toJson(tracksInSearchHistory))
        }
    }

    private fun getTracksFromSharPref() {
        tracksInSearchHistory.clear()
        val json: String? = sharPref.getString(KEY_FOR_ARRAY_WITH_SEARCH_HISTORY, null)
        val listType = object : TypeToken<ArrayList<Track>>() {}.type
        if (json != null) tracksInSearchHistory.addAll(Gson().fromJson(json, listType))
    }

    fun removeAllTracks() {
        tracksInSearchHistory.clear()
        sharPref.edit { clear() }
    }

}