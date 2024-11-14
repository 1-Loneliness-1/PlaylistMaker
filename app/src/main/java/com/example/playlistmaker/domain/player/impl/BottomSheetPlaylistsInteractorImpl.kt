package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.player.BottomSheetPlaylistsInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class BottomSheetPlaylistsInteractorImpl(
    private val playlistsRepository: PlaylistsRepository
) : BottomSheetPlaylistsInteractor {

    override fun addNewPlaylistInDatabase(playlist: Playlist) {
        playlistsRepository.insertNewPlaylist(playlist)
    }

    override fun getAllAvailablePlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getAllPlaylists()
    }

    override fun addNewTrackInPlaylist(
        trackForAdd: Track,
        selectedPlaylist: Playlist
    ): Flow<String> {
        return playlistsRepository.addNewTrackInPlaylist(trackForAdd, selectedPlaylist)
    }

}