package com.example.playlistmaker.data.shar_pref.impl

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import com.example.playlistmaker.data.shar_pref.SharPrefRepository
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

    override fun <T> getArrayListFromResource(): ArrayList<T> {
        val json: String? = sharPref.getString(key, null)
        val listType = object : TypeToken<ArrayList<T>>() {}.type
        return if (json != null) Gson().fromJson(json, listType) else ArrayList()
    }

    override fun <T> putArrayListInSharPref(res: ArrayList<T>) {
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