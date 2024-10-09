package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.search.SharPrefInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.SearchScreenState
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val sharPrefInteractor: SharPrefInteractor
) : ViewModel() {

    private val searchScreenStateLiveData = MutableLiveData<SearchScreenState>(SearchScreenState.Waiting(getTracksFromSharPref()))

    fun getSearchScreenStateLiveData(): LiveData<SearchScreenState> = searchScreenStateLiveData

    fun saveNewTrack(track: Track) {
        val tracksInSearchHistory = getTracksFromSharPref()

        tracksInSearchHistory.removeIf { it.trackId == track.trackId }

        if (tracksInSearchHistory.size == MAX_COUNT_OF_TRACKS_IN_SEARCH_HISTORY) {
            tracksInSearchHistory.removeAt(tracksInSearchHistory.lastIndex)
        }

        tracksInSearchHistory.add(0, track)
        sharPrefInteractor.putResToSharPref(tracksInSearchHistory)
    }

    private fun getTracksFromSharPref(): ArrayList<Track> =
        sharPrefInteractor.getResFromSharPref()

    fun removeAllTracks() {
        sharPrefInteractor.removeAllRes()
    }

    fun getTracksForList(exp: String) {
        if (exp.isNotEmpty()) {
            searchScreenStateLiveData.postValue(SearchScreenState.Loading)
            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(exp)
                    .collect { listOfTracks ->
                        searchScreenStateLiveData.postValue(SearchScreenState.Content(listOfTracks))
                    }
            }
        }
    }

    fun setWaitingStateForScreen() {
        searchScreenStateLiveData.postValue(SearchScreenState.Waiting(getTracksFromSharPref()))
    }

    fun registerChangeListener(listener: () -> Unit) =
        sharPrefInteractor.registerChangeListener(listener)

    fun unregisterChangeListener() =
        sharPrefInteractor.unregisterChangeListener()

    companion object {
        const val MAX_COUNT_OF_TRACKS_IN_SEARCH_HISTORY = 10
    }

}