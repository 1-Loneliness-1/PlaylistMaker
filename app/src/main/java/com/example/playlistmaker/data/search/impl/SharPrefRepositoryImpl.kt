package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import com.example.playlistmaker.data.search.SharPrefRepository
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharPrefRepositoryImpl(
    override val sharPref: SharedPreferences
) : SharPrefRepository {

    private var listener: OnSharedPreferenceChangeListener =
        OnSharedPreferenceChangeListener { _, _ ->

        }

    companion object {
        private const val KEY_FOR_ARRAY_WITH_SEARCH_HISTORY = "elems_in_search_history"
    }

    override fun getArrayListFromResource(): ArrayList<Track> {
        val json: String? = sharPref.getString(KEY_FOR_ARRAY_WITH_SEARCH_HISTORY, null)
        val listType = object : TypeToken<ArrayList<Track>>() {}.type
        return if (json != null) Gson().fromJson(json, listType) else ArrayList()
    }

    override fun putArrayListInSharPref(res: ArrayList<Track>) {
        sharPref.edit {
            putString(KEY_FOR_ARRAY_WITH_SEARCH_HISTORY, Gson().toJson(res))
        }
    }

    override fun removeAllResources() {
        sharPref.edit { clear() }
    }

    override fun registerChangeListener(listener: () -> Unit) {
        this.listener = OnSharedPreferenceChangeListener { _, _ ->
            listener.invoke()
        }
        sharPref.registerOnSharedPreferenceChangeListener(this.listener)
    }

    override fun unregisterChangeListener() {
        sharPref.unregisterOnSharedPreferenceChangeListener(this.listener)
    }
}