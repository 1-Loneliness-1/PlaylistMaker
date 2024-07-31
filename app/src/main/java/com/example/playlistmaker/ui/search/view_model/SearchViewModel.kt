package com.example.playlistmaker.ui.search.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.search.SharPrefInteractor
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.SearchScreenState
import com.example.playlistmaker.domain.search.model.Track

class SearchViewModel(
    application: Application,
    private val tracksInteractor: TracksInteractor,
    val sharPrefInteractor: SharPrefInteractor
) : AndroidViewModel(application) {

    private val searchScreenStateLiveData =
        MutableLiveData<SearchScreenState>(SearchScreenState.Waiting)

    fun getSearchScreenStateLiveData(): LiveData<SearchScreenState> = searchScreenStateLiveData

    companion object {
        fun getViewModelFactory(
            app: Application,
            nameOfFile: String,
            key: String
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    app,
                    Creator.provideTracksInteractor(),
                    Creator.provideSharPrefInteractor(app, nameOfFile, key)
                )
            }
        }
    }

    fun getTracksForList(exp: String, consume: (List<Track>) -> Unit) {
        searchScreenStateLiveData.postValue(SearchScreenState.Loading)
        tracksInteractor.searchTracks(exp, consume)
    }

    fun setWaitingStateForScreen() {
        searchScreenStateLiveData.postValue(SearchScreenState.Waiting)
    }

    fun setContentStateOfScreen(listOfTracks: List<Track>) {
        searchScreenStateLiveData.postValue(SearchScreenState.Content(listOfTracks))
    }

    fun registerChangeListener(listener: () -> Unit) =
        sharPrefInteractor.registerChangeListener(listener)

    fun unregisterChangeListener() =
        sharPrefInteractor.unregisterChangeListener()
}