package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.player.impl.PlayerInteractor
import com.example.playlistmaker.domain.player.model.PlayerScreenState

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val trackInfoInteractor
) : ViewModel() {
    private var loadingLiveData = MutableLiveData(true)
    private var screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)

    fun getLoadingLiveData(): LiveData<Boolean> = loadingLiveData

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {

        }
    }
}