package com.example.playlistmaker.domain.playlist.impl

import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.db.TracksInPlaylistsRepository
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.playlist.TracksInPlaylistInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class TracksInPlaylistInteractorImpl(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksInPlaylistsRepository: TracksInPlaylistsRepository
) : TracksInPlaylistInteractor {

    override fun deleteTrackFromPlaylist(selectedPlaylistId: Long, deletedTrack: Track) {
        tracksInPlaylistsRepository.deleteTrackFromPlaylist(selectedPlaylistId, deletedTrack)
    }

    override fun deletePlaylist(deletedPlaylistId: Long) {
        playlistsRepository.deletePlaylist(deletedPlaylistId)
    }

    override fun getPlaylistInfoById(selectedPlaylistId: Long): Flow<Playlist> {
        return playlistsRepository.getPlaylistInfoById(selectedPlaylistId)
    }

    override fun getAllTracksInPlaylist(selectedPlaylistId: Long): Flow<List<Track>> {
        return tracksInPlaylistsRepository.getAllTracksInPlaylist(selectedPlaylistId)
    }

}