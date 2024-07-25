package com.example.playlistmaker.data.shar_pref.impl

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.data.shar_pref.SharPrefRepository
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharPrefRepositoryImpl(
    override val app: Application,
    override val nameOfFile: String,
    override val key: String
) : SharPrefRepository {

    private val sharPref = app.getSharedPreferences(nameOfFile, MODE_PRIVATE)

    override fun getResource(): Any {
        val json: String? = sharPref.getString(key, null)
        val listType = object : TypeToken<ArrayList<Track>>() {}.type
        return if (json != null) Gson().fromJson(json, listType) else ArrayList<Track>()
    }

    override fun putResource(res: Any) {
        TODO("Not yet implemented")
    }

    override fun removeAllResources() {
        TODO("Not yet implemented")
    }
}