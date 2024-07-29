package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApiService::class.java)

    override fun doRequest(dto: Any): Response {
        var listOfTracks = Response()
        when (dto) {
            is TracksSearchRequest -> {
                itunesService.search(dto.searchExpression)
                    .enqueue(object : Callback<TracksSearchResponse> {
                        override fun onResponse(
                            call: Call<TracksSearchResponse>,
                            response: retrofit2.Response<TracksSearchResponse>
                        ) {
                            if (response.code() == 200) {
                                listOfTracks =
                                    response.body()!!.apply { resultCode = response.code() }
                            } else {
                                listOfTracks = Response().apply { resultCode = response.code() }
                            }
                        }

                        override fun onFailure(p0: Call<TracksSearchResponse>, p1: Throwable) {
                            listOfTracks = Response()
                        }

                    })
                return listOfTracks
            }
            else -> {
                return Response().apply { resultCode = 400 }
            }
        }
    }

}