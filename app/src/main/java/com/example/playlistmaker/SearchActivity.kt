package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.model.TrackResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
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
    private var searchTracksRecycler: RecyclerView? = null
    private var searchHistoryRecycler: RecyclerView? = null
    private var clearHistoryButton: Button? = null
    private var youLookedForText: TextView? = null
    private var sharPref: SharedPreferences? = null
    private var listener: OnSharedPreferenceChangeListener? = null


    companion object {
        const val TEXT_IN_SEARCH_EDIT_TEXT = "TEXT_IN_SEARCH_EDIT_TEXT"
        const val NAME_FOR_FILE_WITH_SEARCH_HISTORY = "search_history"
        private val trackList = ArrayList<Track>()
        private val searchHistoryList = ArrayList<Track>()
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"
        private var ADAPTER: TrackAdapter? = null
        private var SEARCH_HISTORY_ADAPTER: TrackAdapter? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backToPreviousScreenButton = findViewById<ImageView>(R.id.ivBackToPreviousScreen)
        searchEditText = findViewById(R.id.etSearch)
        clearSearchEditTextButton = findViewById(R.id.ivClearEditText)
        searchTracksRecycler = findViewById(R.id.rvTrackSearchList)
        searchHistoryRecycler = findViewById(R.id.rvSearchHistory)
        noResultsPlaceholder = findViewById(R.id.ivNothingFoundPlaceholder)
        noInternetPlaceholder = findViewById(R.id.ivNoInternetPlaceholder)
        errorNothingFoundText = findViewById(R.id.tvErrorNothingFound)
        errorNoInternetText = findViewById(R.id.tvErrorNoInternet)
        refreshButton = findViewById(R.id.bRefreshRequest)
        clearHistoryButton = findViewById(R.id.bClearSearchHistory)
        youLookedForText = findViewById(R.id.tvYouLookedFor)

        sharPref = getSharedPreferences(NAME_FOR_FILE_WITH_SEARCH_HISTORY, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharPref!!)

        ADAPTER = TrackAdapter(trackList, sharPref!!, searchHistory)
        searchTracksRecycler?.adapter = ADAPTER

        //Setting layout manager and adapter for search history list
        searchHistoryRecycler?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        searchHistoryList.addAll(searchHistory.tracksInSearchHistory)
        SEARCH_HISTORY_ADAPTER = TrackAdapter(searchHistoryList, sharPref!!, searchHistory)
        searchHistoryRecycler?.adapter = SEARCH_HISTORY_ADAPTER

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
            ADAPTER?.notifyDataSetChanged()
        }

        val textWatcherForSearchEditText = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Nothing to do for now
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Nothing to do for now
            }

            override fun afterTextChanged(s: Editable?) {
                clearSearchEditTextButton?.isVisible = s?.isNotEmpty() == true

                searchHistoryRecycler?.isVisible = s?.isEmpty() == true &&
                        searchHistoryList.isNotEmpty()
                youLookedForText?.isVisible = s?.isEmpty() == true &&
                        searchHistoryList.isNotEmpty()
                clearHistoryButton?.isVisible = s?.isEmpty() == true &&
                        searchHistoryList.isNotEmpty()
                searchTracksRecycler?.isVisible = s?.isNotEmpty() == true

                //Saving current text in edit text in variable for putting in Instance State
                currentTextInEditText = s.toString()
            }

        }
        searchEditText?.addTextChangedListener(textWatcherForSearchEditText)
        searchEditText?.setOnFocusChangeListener { _, hasFocus ->
            val conditionOfSearchEditText = hasFocus &&
                    searchEditText?.text?.isEmpty() == true &&
                    searchHistoryList.isNotEmpty()
            searchHistoryRecycler?.isVisible = conditionOfSearchEditText
            youLookedForText?.isVisible = conditionOfSearchEditText
            clearHistoryButton?.isVisible = conditionOfSearchEditText
            searchTracksRecycler?.isVisible = !conditionOfSearchEditText
        }
        searchEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendRequest()
                true
            }
            false
        }

        //Implementation of on click listener for clear search history button
        clearHistoryButton?.setOnClickListener {
            searchHistory.removeAllTracks()
            searchHistoryList.clear()
            SEARCH_HISTORY_ADAPTER?.notifyDataSetChanged()
            searchHistoryRecycler?.isVisible = false
            youLookedForText?.isVisible = false
            clearHistoryButton?.isVisible = false
            searchTracksRecycler?.isVisible = true
        }

        //Implementation of change listener for instance of shared preferences
        listener = OnSharedPreferenceChangeListener { _, _ ->
            searchHistoryList.clear()
            searchHistoryList.addAll(searchHistory.tracksInSearchHistory)
            SEARCH_HISTORY_ADAPTER?.notifyDataSetChanged()
        }

    }

    override fun onResume() {
        super.onResume()

        sharPref?.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()

        sharPref?.unregisterOnSharedPreferenceChangeListener(listener)
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
                        ADAPTER?.notifyDataSetChanged()

                        searchEditText?.isFocusableInTouchMode = true
                        clearSearchEditTextButton?.isEnabled = true
                        clearSearchEditTextButton?.isClickable = true

                        noInternetPlaceholder?.isVisible = false
                        noResultsPlaceholder?.isVisible = response.body()?.resultCount == 0
                        errorNothingFoundText?.isVisible = response.body()?.resultCount == 0
                        errorNoInternetText?.isVisible = false
                        refreshButton?.isVisible = false
                        searchTracksRecycler?.isVisible = response.body()?.resultCount != 0
                        youLookedForText?.isVisible = false
                        searchHistoryRecycler?.isVisible = false
                        clearHistoryButton?.isVisible = false
                    } else {
                        trackList.clear()
                        ADAPTER?.notifyDataSetChanged()

                        searchEditText?.isFocusable = false
                        clearSearchEditTextButton?.isEnabled = false
                        clearSearchEditTextButton?.isClickable = false

                        noResultsPlaceholder?.isVisible = false
                        noInternetPlaceholder?.isVisible = true
                        errorNothingFoundText?.isVisible = false
                        errorNoInternetText?.isVisible = true
                        refreshButton?.isVisible = true
                        searchTracksRecycler?.isVisible = false
                        youLookedForText?.isVisible = false
                        searchHistoryRecycler?.isVisible = false
                        clearHistoryButton?.isVisible = false
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    trackList.clear()
                    ADAPTER?.notifyDataSetChanged()

                    searchEditText?.isFocusable = false
                    clearSearchEditTextButton?.isEnabled = false
                    clearSearchEditTextButton?.isClickable = false

                    noResultsPlaceholder?.isVisible = false
                    noInternetPlaceholder?.isVisible = true
                    errorNothingFoundText?.isVisible = false
                    errorNoInternetText?.isVisible = true
                    refreshButton?.isVisible = true
                    searchTracksRecycler?.isVisible = false
                    youLookedForText?.isVisible = false
                    searchHistoryRecycler?.isVisible = false
                    clearHistoryButton?.isVisible = false
                }

            })
    }

}