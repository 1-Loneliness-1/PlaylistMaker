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
    private val sharPrefInteractor: SharPrefInteractor
) : AndroidViewModel(application) {

    private val searchScreenStateLiveData =
        MutableLiveData<SearchScreenState>(SearchScreenState.Loading)

    fun getSearchScreenStateLiveData(): LiveData<SearchScreenState> = searchScreenStateLiveData

    companion object {
        fun getViewModelFactory(app: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(
                    app,
                    Creator.provideTracksInteractor(),
                    Creator.provideSharPrefInteractor(app)
                )
            }
        }
    }

    fun getTracksForList(exp: String) {
        searchScreenStateLiveData.postValue(
            SearchScreenState.Content(tracksInteractor.searchTracks(exp))
        )
    }

    fun getValForSharPref(): Any =
        sharPrefInteractor.getResFromSharPref()

    fun putValInSharPref(trackForSave: Track) =
        sharPrefInteractor.putResToSharPref(trackForSave)

    fun removeAllValFromSharPref() =
        sharPrefInteractor.removeAllRes()
}