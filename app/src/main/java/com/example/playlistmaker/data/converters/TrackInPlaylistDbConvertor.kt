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

    fun map(trackInPlaylist: Track): TrackEntity =
        TrackEntity(
            trackId = trackInPlaylist.trackId,
            trackTitle = trackInPlaylist.trackName,
            artistName = trackInPlaylist.artistName,
            trackTimeMillis = trackInPlaylist.trackTimeMillis,
            album = trackInPlaylist.collectionName,
            releaseDate = trackInPlaylist.releaseDate,
            genre = trackInPlaylist.primaryGenreName,
            country = trackInPlaylist.country,
            artworkUrl100 = trackInPlaylist.artworkUrl100,
            previewUrl = trackInPlaylist.previewUrl
        )

}