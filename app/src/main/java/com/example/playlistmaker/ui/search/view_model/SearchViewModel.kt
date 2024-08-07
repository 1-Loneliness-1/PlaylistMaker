package com.example.playlistmaker.ui.search.view_model

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.search.SharPrefInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.SearchScreenState
import com.example.playlistmaker.domain.search.model.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val sharPrefInteractor: SharPrefInteractor
) : ViewModel() {

    private val searchScreenStateLiveData = MutableLiveData<SearchScreenState>(SearchScreenState.Waiting(getTracksFromSharPref()))

    fun getSearchScreenStateLiveData(): LiveData<SearchScreenState> = searchScreenStateLiveData

    companion object {
        const val MAX_COUNT_OF_TRACKS_IN_SEARCH_HISTORY = 10

        fun getViewModelFactory(
            sharPref: SharedPreferences,
            key: String
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    Creator.provideTracksInteractor(),
                    Creator.provideSharPrefInteractor(sharPref, key)
                )
            }
        }
    }

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

    fun getTracksForList(exp: String, consume: (List<Track>) -> Unit) {
        searchScreenStateLiveData.postValue(SearchScreenState.Loading)
        tracksInteractor.searchTracks(exp, consume)
    }

    fun setWaitingStateForScreen() {
        searchScreenStateLiveData.postValue(SearchScreenState.Waiting(getTracksFromSharPref()))
    }

    fun setContentStateOfScreen(listOfTracks: List<Track>) {
        searchScreenStateLiveData.postValue(SearchScreenState.Content(listOfTracks))
    }

    fun registerChangeListener(listener: () -> Unit) =
        sharPrefInteractor.registerChangeListener(listener)

    fun unregisterChangeListener() =
        sharPrefInteractor.unregisterChangeListener()
}