package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.media.model.Playlist

class PlaylistDbConvertor {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistTitle = playlist.playlistTitle,
            playlistDescription = playlist.playlistDescription,
            playlistCoverPath = playlist.playlistCoverPath,
            listOfTracksInPlaylist = playlist.listOfTracksInPlaylist,
            trackCountInPlaylist = playlist.trackCountInPlaylist
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.playlistId,
            playlistEntity.playlistTitle,
            playlistEntity.playlistDescription,
            playlistEntity.playlistCoverPath,
            playlistEntity.listOfTracksInPlaylist,
            playlistEntity.trackCountInPlaylist
        )
    }

}