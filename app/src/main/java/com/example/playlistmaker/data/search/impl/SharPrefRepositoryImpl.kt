package com.example.playlistmaker.data.search.impl

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import com.example.playlistmaker.data.search.SharPrefRepository
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharPrefRepositoryImpl(
    override val app: Application,
    override val nameOfFile: String,
    override val key: String
) : SharPrefRepository {

    private val sharPref = app.getSharedPreferences(nameOfFile, MODE_PRIVATE)
    private var listener: OnSharedPreferenceChangeListener =
        OnSharedPreferenceChangeListener() { _, _ ->

        }

    override fun getArrayListFromResource(): ArrayList<Track> {
        val json: String? = sharPref.getString(key, null)
        val listType = object : TypeToken<ArrayList<Track>>() {}.type
        return if (json != null) Gson().fromJson(json, listType) else ArrayList()
    }

    override fun putArrayListInSharPref(res: ArrayList<Track>) {
        sharPref.edit {
            putString(key, Gson().toJson(res))
        }
    }

    override fun removeAllResources() {
        sharPref.edit { clear() }
    }

    override fun registerChangeListener(listener: () -> Unit) {
        this.listener = OnSharedPreferenceChangeListener() { _, _ ->
            listener.invoke()
        }
        sharPref.registerOnSharedPreferenceChangeListener(this.listener)
    }

    override fun unregisterChangeListener() {
        sharPref.unregisterOnSharedPreferenceChangeListener(this.listener)
    }
}