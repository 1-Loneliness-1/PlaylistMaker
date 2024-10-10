package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun doRequest(dto: Any): Response {
        return when (dto) {
            is TracksSearchRequest -> {
                withContext(Dispatchers.IO) {
                    try {
                        itunesService.search(dto.searchExpression).apply { resultCode = 200 }
                    } catch (e: Throwable) {
                        Response().apply { resultCode = 500 }
                    }
                }
            }

            else -> {
                Response().apply { resultCode = 400 }
            }
        }
    }

}