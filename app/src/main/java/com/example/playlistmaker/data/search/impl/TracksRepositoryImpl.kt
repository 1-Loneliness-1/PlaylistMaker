package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<List<Track>> {
        return flow {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            when (response.resultCode) {
                HTTP_OK_CODE -> {
                    with(response as TracksSearchResponse) {
                        val listOfFoundedTracks = results.map {
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
                        emit(listOfFoundedTracks)
                    }
                }

                else -> {
                    emit(
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
    }

    companion object {
        const val HTTP_OK_CODE = 200
    }

}