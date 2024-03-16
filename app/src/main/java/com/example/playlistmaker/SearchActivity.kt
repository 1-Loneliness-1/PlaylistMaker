package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.model.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val adapter = TrackAdapter(trackList)

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesApiService::class.java)

    private var searchEditText: EditText? = null
    private var currentTextInEditText: String? = ""
    private var noResultsPlaceholder: ImageView? = null
    private var noInternetPlaceholder: ImageView? = null
    private var errorNothingFoundText: TextView? = null
    private var errorNoInternetText: TextView? = null
    private var refreshButton: Button? = null
    private var clearSearchEditTextButton: ImageView? = null


    companion object {
        const val TEXT_IN_SEARCH_EDIT_TEXT = "TEXT_IN_SEARCH_EDIT_TEXT"
        private val trackList = ArrayList<Track>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backToPreviousScreenButton = findViewById<ImageView>(R.id.ivBackToPreviousScreen)
        searchEditText = findViewById(R.id.etSearch)
        clearSearchEditTextButton = findViewById(R.id.ivClearEditText)
        val searchTracksRecycler = findViewById<RecyclerView>(R.id.rvTrackSearchList)
        noResultsPlaceholder = findViewById(R.id.ivNothingFoundPlaceholder)
        noInternetPlaceholder = findViewById(R.id.ivNoInternetPlaceholder)
        errorNothingFoundText = findViewById(R.id.tvErrorNothingFound)
        errorNoInternetText = findViewById(R.id.tvErrorNoInternet)
        refreshButton = findViewById(R.id.bRefreshRequest)

        searchTracksRecycler.adapter = adapter

        backToPreviousScreenButton.setOnClickListener { finish() }

        refreshButton?.setOnClickListener { sendRequest() }

        clearSearchEditTextButton?.setOnClickListener {
            searchEditText?.setText("")
            clearSearchEditTextButton?.isVisible = false
            searchEditText?.clearFocus()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)

            noInternetPlaceholder?.isVisible = false
            noResultsPlaceholder?.isVisible = false
            errorNothingFoundText?.isVisible = false
            errorNoInternetText?.isVisible = false
            refreshButton?.isVisible = false
            trackList.clear()
            adapter.notifyDataSetChanged()
        }

        searchEditText?.doOnTextChanged { text, _, _, _ ->
            clearSearchEditTextButton?.isVisible = !text.isNullOrEmpty()

            //Saving current text in edit text in variable for putting in Instance State
            currentTextInEditText = text.toString()
        }
        searchEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendRequest()
                true
            }
            false
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(TEXT_IN_SEARCH_EDIT_TEXT, currentTextInEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        currentTextInEditText = savedInstanceState.getString(TEXT_IN_SEARCH_EDIT_TEXT)
        searchEditText?.setText(currentTextInEditText)
    }

    //Fun for making request
    private fun sendRequest() {

        itunesService.search(searchEditText?.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        trackList.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()

                        searchEditText?.isFocusableInTouchMode = true
                        clearSearchEditTextButton?.isEnabled = true
                        clearSearchEditTextButton?.isClickable = true

                        noInternetPlaceholder?.isVisible = false
                        noResultsPlaceholder?.isVisible = response.body()?.resultCount == 0
                        errorNothingFoundText?.isVisible = response.body()?.resultCount == 0
                        errorNoInternetText?.isVisible = false
                        refreshButton?.isVisible = false
                    } else {
                        trackList.clear()
                        adapter.notifyDataSetChanged()

                        searchEditText?.isFocusable = false
                        clearSearchEditTextButton?.isEnabled = false
                        clearSearchEditTextButton?.isClickable = false

                        noResultsPlaceholder?.isVisible = false
                        noInternetPlaceholder?.isVisible = true
                        errorNothingFoundText?.isVisible = false
                        errorNoInternetText?.isVisible = true
                        refreshButton?.isVisible = true
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    trackList.clear()
                    adapter.notifyDataSetChanged()

                    searchEditText?.isFocusable = false
                    clearSearchEditTextButton?.isEnabled = false
                    clearSearchEditTextButton?.isClickable = false

                    noResultsPlaceholder?.isVisible = false
                    noInternetPlaceholder?.isVisible = true
                    errorNothingFoundText?.isVisible = false
                    errorNoInternetText?.isVisible = true
                    refreshButton?.isVisible = true
                }

            })
    }

}