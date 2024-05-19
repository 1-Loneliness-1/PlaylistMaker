package com.example.playlistmaker.presentation.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.data.network.ItunesApiService
import com.example.playlistmaker.domain.entities.Track
import com.example.playlistmaker.domain.entities.TrackResponse
import com.example.playlistmaker.presentation.player.PlayerActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(App.ITUNES_BASE_URL)
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
    private var progressBarTrackListLoading: ProgressBar? = null
    private var isNotPressed = true
    private lateinit var sharPref: SharedPreferences
    private lateinit var listener: OnSharedPreferenceChangeListener
    private lateinit var adapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private val mainHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { sendRequest() }
    private val tapEnableRunnable = Runnable { isNotPressed = true }


    companion object {
        const val TEXT_IN_SEARCH_EDIT_TEXT = "TEXT_IN_SEARCH_EDIT_TEXT"
        const val NAME_FOR_FILE_WITH_SEARCH_HISTORY = "search_history"
        private const val KEY_FOR_INTENT_DATA = "Selected track"
        private val trackList = ArrayList<Track>()
        private val searchHistoryList = ArrayList<Track>()
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val TAP_DEBOUNCE_DELAY = 1000L
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
        progressBarTrackListLoading = findViewById(R.id.pbListOfTracksLoading)

        sharPref = getSharedPreferences(NAME_FOR_FILE_WITH_SEARCH_HISTORY, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharPref)

        adapter = TrackAdapter(trackList) {
            if (isNotPressed) {
                tapDebounce()
                searchHistory.saveNewTrack(it)

                //Implementation of putting information for Player Activity by putExtra fun in Intent
                val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                playerIntent.putExtra(KEY_FOR_INTENT_DATA, Gson().toJson(it))
                startActivity(playerIntent)
            }
        }
        searchTracksRecycler?.adapter = adapter

        //Setting adapter for search history list
        searchHistoryList.addAll(searchHistory.tracksInSearchHistory)
        searchHistoryAdapter = TrackAdapter(searchHistoryList) {
            if (isNotPressed) {
                tapDebounce()
                searchHistory.saveNewTrack(it)

                //Implementation of putting information for Player Activity by putExtra fun in Intent
                val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                playerIntent.putExtra(KEY_FOR_INTENT_DATA, Gson().toJson(it))
                startActivity(playerIntent)
            }
        }
        searchHistoryRecycler?.adapter = searchHistoryAdapter

        backToPreviousScreenButton.setOnClickListener { finish() }

        refreshButton?.setOnClickListener { sendRequest() }

        clearSearchEditTextButton?.setOnClickListener {
            mainHandler.removeCallbacksAndMessages(searchRunnable)
            searchEditText?.setText("")
            clearSearchEditTextButton?.isVisible = false
            progressBarTrackListLoading?.isVisible = false
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

        searchEditText?.doAfterTextChanged { s ->
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

            searchDebounce()
        }
        searchEditText?.setOnFocusChangeListener { _, hasFocus ->
            val conditionOfSearchEditText = hasFocus &&
                    searchEditText?.text?.isEmpty() == true &&
                    searchHistoryList.isNotEmpty()
            changeVisibilityOfSearchHistoryElements(conditionOfSearchEditText)
        }

        //Implementation of on click listener for clear search history button
        clearHistoryButton?.setOnClickListener {
            searchHistory.removeAllTracks()
            searchHistoryList.clear()
            searchHistoryAdapter.notifyDataSetChanged()
            changeVisibilityOfSearchHistoryElements(false)
        }

        //Implementation of change listener for instance of shared preferences
        listener = OnSharedPreferenceChangeListener { _, _ ->
            searchHistoryList.clear()
            searchHistoryList.addAll(searchHistory.tracksInSearchHistory)
            searchHistoryAdapter.notifyDataSetChanged()
        }

    }

    override fun onResume() {
        super.onResume()

        sharPref.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()

        sharPref.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(searchRunnable)
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
        progressBarTrackListLoading?.isVisible = true
        searchTracksRecycler?.isVisible = false
        noInternetPlaceholder?.isVisible = false
        noResultsPlaceholder?.isVisible = false
        errorNothingFoundText?.isVisible = false
        errorNoInternetText?.isVisible = false
        refreshButton?.isVisible = false
        searchTracksRecycler?.isVisible = false
        youLookedForText?.isVisible = false
        searchHistoryRecycler?.isVisible = false
        clearHistoryButton?.isVisible = false
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

                        progressBarTrackListLoading?.isVisible = false

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
                        adapter.notifyDataSetChanged()

                        progressBarTrackListLoading?.isVisible = false

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
                    adapter.notifyDataSetChanged()

                    progressBarTrackListLoading?.isVisible = false

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

    //Implementation of fun for changing visibility of elements on sending request
    private fun changeVisibilityOfSearchHistoryElements(isSearchNotEmpty: Boolean) {
        searchHistoryRecycler?.isVisible = isSearchNotEmpty
        youLookedForText?.isVisible = isSearchNotEmpty
        clearHistoryButton?.isVisible = isSearchNotEmpty
        searchTracksRecycler?.isVisible = !isSearchNotEmpty
    }

    private fun searchDebounce() {
        mainHandler.removeCallbacksAndMessages(searchRunnable)
        if (searchEditText?.text?.isNotEmpty() == true)
            mainHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun tapDebounce() {
        isNotPressed = false
        mainHandler.postDelayed(tapEnableRunnable, TAP_DEBOUNCE_DELAY)
    }
}