package com.example.playlistmaker.domain.player.model

import com.example.playlistmaker.domain.media.model.Playlist

sealed class PlaylistsBottomSheetScreenState {

    data class ContentState(val availablePlaylists: List<Playlist>) :
        PlaylistsBottomSheetScreenState()

}