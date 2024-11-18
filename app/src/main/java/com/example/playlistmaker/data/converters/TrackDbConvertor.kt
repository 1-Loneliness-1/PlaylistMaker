package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.domain.search.model.Track

class TrackDbConvertor {

    fun map(track: Track): FavoriteTrackEntity =
        FavoriteTrackEntity(
            track.trackId.toString(),
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.artworkUrl100,
            track.previewUrl
        )

    fun map(track: FavoriteTrackEntity): Track =
        Track(
            track.trackId.toLong(),
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.artworkUrl100,
            track.previewUrl
        )

}