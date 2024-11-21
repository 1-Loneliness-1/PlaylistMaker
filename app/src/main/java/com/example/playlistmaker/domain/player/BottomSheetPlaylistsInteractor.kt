package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface BottomSheetPlaylistsInteractor {

    fun addNewPlaylistInDatabase(playlist: Playlist)

    fun getAllAvailablePlaylists(): Flow<List<Playlist>>

    fun insertNewTrack(updatedPlaylistId: Long, insertedTrack: Track): Flow<String>

}