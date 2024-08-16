package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTracks(expression: String, consume: (List<Track>) -> Unit) {

        val cons = { resp: Response ->
            when (resp.resultCode) {
                200 -> {
                    consume(
                        (resp as TracksSearchResponse).results.map {
                            Track(
                                it.trackId,
                                it.trackName,
                                it.artistName,
                                SimpleDateFormat(
                                    "mm:ss",
                                    Locale.getDefault()
                                ).format(it.trackTimeMillis),
                                it.collectionName,
                                it.releaseDate,
                                it.primaryGenreName,
                                it.country,
                                it.artworkUrl100,
                                it.previewUrl
                            )
                        }
                    )
                }

                else -> {
                    consume(
                        listOf(
                            Track(
                                -1,
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                ""
                            )
                        )
                    )
                }
            }
        }

        networkClient.doRequest(TracksSearchRequest(expression), cons)
    }

}