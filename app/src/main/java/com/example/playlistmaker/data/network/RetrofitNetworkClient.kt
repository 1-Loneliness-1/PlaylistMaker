package com.example.playlistmaker.data.network

import com.example.playlistmaker.App
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(App.ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApiService::class.java)

    override fun doRequest(dto: Any): Response {
        when (dto) {
            is TracksSearchRequest -> {
                val resp = itunesService.search(dto.searchExpression).execute()

                val body = resp.body() ?: Response()

                return body.apply { resultCode = resp.code() }
            }

            else -> {
                return Response().apply { resultCode = 400 }
            }
        }
    }
}