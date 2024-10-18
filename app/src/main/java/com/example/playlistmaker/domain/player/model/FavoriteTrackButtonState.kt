package com.example.playlistmaker.domain.player.model

sealed class FavoriteTrackButtonState {

    data object FavoriteState : FavoriteTrackButtonState()

    data object IsNotFavoriteState : FavoriteTrackButtonState()

}