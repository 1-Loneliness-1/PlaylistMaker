package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.search.model.Track

class TrackInPlaylistDbConvertor {

    fun map(trackInPlaylist: TrackEntity): Track =
        Track(
            trackInPlaylist.trackId,
            trackInPlaylist.trackTitle,
            trackInPlaylist.artistName,
            trackInPlaylist.trackTimeMillis,
            trackInPlaylist.album,
            trackInPlaylist.releaseDate,
            trackInPlaylist.genre,
            trackInPlaylist.country,
            trackInPlaylist.artworkUrl100,
            trackInPlaylist.previewUrl
        )

    fun map(updatedPlaylistId: Long, trackInPlaylist: Track): TrackEntity =
        TrackEntity(
            trackInPlaylist.trackId,
            updatedPlaylistId,
            trackInPlaylist.trackName,
            trackInPlaylist.artistName,
            trackInPlaylist.trackTimeMillis,
            trackInPlaylist.collectionName,
            trackInPlaylist.releaseDate,
            trackInPlaylist.primaryGenreName,
            trackInPlaylist.country,
            trackInPlaylist.artworkUrl100,
            trackInPlaylist.previewUrl
        )

}