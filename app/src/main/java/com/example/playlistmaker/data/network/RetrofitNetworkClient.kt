package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

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
                        itunesService.search(dto.searchExpression)
                            .apply { resultCode = HTTP_OK_CODE }
                    } catch (e: Throwable) {
                        Response().apply { resultCode = HTTP_INTERNAL_SERVER_ERROR_CODE }
                    }
                }
            }

            else -> {
                Response().apply { resultCode = HTTP_BAD_REQUEST_CODE }
            }
        }
    }

    companion object {
        const val ITUNES_BASE_URL = "https://itunes.apple.com"
        const val HTTP_OK_CODE = 200
        const val HTTP_BAD_REQUEST_CODE = 400
        const val HTTP_INTERNAL_SERVER_ERROR_CODE = 500
    }

}