package com.example.playlistmaker.ui.search.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.search.model.SearchScreenState
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private var isNotPressed = true
    private var currentTextInEditText: String = ""
    private lateinit var searchEditText: EditText
    private lateinit var noResultsPlaceholder: ImageView
    private lateinit var noInternetPlaceholder: ImageView
    private lateinit var errorNothingFoundText: TextView
    private lateinit var errorNoInternetText: TextView
    private lateinit var refreshButton: Button
    private lateinit var clearSearchEditTextButton: ImageView
    private lateinit var searchTracksRecycler: RecyclerView
    private lateinit var searchHistoryRecycler: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var youLookedForText: TextView
    private lateinit var progressBarTrackListLoading: ProgressBar
    private lateinit var listener: () -> Unit
    private lateinit var adapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModel()

    private val mainHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        viewModel.getTracksForList(
            currentTextInEditText,
            consume = { listOfTracks ->
                changeStateOfScreenToContent(listOfTracks)
            }
        )
    }
    private val tapEnableRunnable = Runnable { isNotPressed = true }

    companion object {
        const val TEXT_IN_SEARCH_EDIT_TEXT = "TEXT_IN_SEARCH_EDIT_TEXT"
        private const val KEY_FOR_INTENT_DATA = "Selected track"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val TAP_DEBOUNCE_DELAY = 1000L
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchEditText = binding.etSearch
        clearSearchEditTextButton = binding.ivClearEditText
        searchTracksRecycler = binding.rvTrackSearchList
        searchHistoryRecycler = binding.rvSearchHistory
        noResultsPlaceholder = binding.ivNothingFoundPlaceholder
        noInternetPlaceholder = binding.ivNoInternetPlaceholder
        errorNothingFoundText = binding.tvErrorNothingFound
        errorNoInternetText = binding.tvErrorNoInternet
        refreshButton = binding.bRefreshRequest
        clearHistoryButton = binding.bClearSearchHistory
        youLookedForText = binding.tvYouLookedFor
        progressBarTrackListLoading = binding.pbListOfTracksLoading

        adapter = TrackAdapter {
            if (isNotPressed) {
                tapDebounce()
                viewModel.saveNewTrack(it)

                //Implementation of putting information for Player Activity by putExtra fun in Intent
                val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                playerIntent.putExtra(KEY_FOR_INTENT_DATA, Gson().toJson(it))
                startActivity(playerIntent)
            }
        }
        searchTracksRecycler.adapter = adapter

        //Setting adapter for search history list
        searchHistoryAdapter = TrackAdapter {
            if (isNotPressed) {
                tapDebounce()
                viewModel.saveNewTrack(it)
                viewModel.setWaitingStateForScreen()

                //Implementation of putting information for Player Activity by putExtra fun in Intent
                val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                playerIntent.putExtra(KEY_FOR_INTENT_DATA, Gson().toJson(it))
                startActivity(playerIntent)
            }
        }
        searchHistoryRecycler.adapter = searchHistoryAdapter

        viewModel.getSearchScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is SearchScreenState.Waiting -> {
                    searchHistoryAdapter.setData(screenState.tracksInSearchHistory)
                    changeVisibilityOfElements(screenState)
                }
                is SearchScreenState.Loading -> {
                    changeVisibilityOfElements(screenState)
                }
                is SearchScreenState.Content -> {
                    changeVisibilityOfElements(screenState)
                    if (screenState.listOfFoundedTracks.isNotEmpty() && screenState.listOfFoundedTracks[0].trackId.toInt() != -1) {
                        adapter.setData(screenState.listOfFoundedTracks)
                    }
                }
            }
        }

        binding.ivBackToPreviousScreen.setOnClickListener { finish() }

        refreshButton.setOnClickListener {
            viewModel.getTracksForList(
                currentTextInEditText,
                consume = { listOfTracks ->
                    changeStateOfScreenToContent(listOfTracks)
                }
            )
        }

        clearSearchEditTextButton.setOnClickListener {
            mainHandler.removeCallbacksAndMessages(searchRunnable)
            searchEditText.setText("")
            clearSearchEditTextButton.isVisible = false
            searchEditText.clearFocus()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)

            viewModel.setWaitingStateForScreen()

            adapter.removeAllData()
        }

        searchEditText.doAfterTextChanged { s ->
            clearSearchEditTextButton.isVisible = s?.isNotEmpty() == true

            //Saving current text in edit text in variable for putting in Instance State
            currentTextInEditText = s.toString()

            if (s?.isNotEmpty() == true) {
                searchDebounce()
            } else {
                mainHandler.removeCallbacksAndMessages(null)
                viewModel.setWaitingStateForScreen()
            }
        }
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            val conditionOfSearchEditText = hasFocus &&
                    searchEditText.text?.isEmpty() == true &&
                    searchHistoryAdapter.itemCount > 0
            changeVisibilityOfSearchHistoryElements(conditionOfSearchEditText)
        }

        //Implementation of on click listener for clear search history button
        clearHistoryButton.setOnClickListener {
            viewModel.removeAllTracks()
            searchHistoryAdapter.removeAllData()
            changeVisibilityOfSearchHistoryElements(false)
        }

        //Implementation of change listener for instance of shared preferences
        listener = {

        }

    }

    override fun onResume() {
        super.onResume()

        viewModel.registerChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()

        viewModel.unregisterChangeListener()
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

        currentTextInEditText = savedInstanceState.getString(TEXT_IN_SEARCH_EDIT_TEXT).toString()
        searchEditText.setText(currentTextInEditText)
    }

    private fun changeVisibilityOfElements(case: SearchScreenState) {

        when (case) {
            is SearchScreenState.Waiting -> {
                progressBarTrackListLoading.isVisible = false
                searchTracksRecycler.isVisible = false

                changeParamsOfSearchEditText(true)

                errorNoInternetText.isVisible = false
                errorNothingFoundText.isVisible = false
                noInternetPlaceholder.isVisible = false
                noResultsPlaceholder.isVisible = false
                refreshButton.isVisible = false

                changeVisibilityOfSearchHistoryElements(currentTextInEditText.isEmpty() && searchHistoryAdapter.itemCount > 0)
            }
            is SearchScreenState.Content -> {

                if (case.listOfFoundedTracks.isEmpty()) {
                    progressBarTrackListLoading.isVisible = false
                    searchTracksRecycler.isVisible = false

                    changeParamsOfSearchEditText(true)

                    errorNoInternetText.isVisible = false
                    noInternetPlaceholder.isVisible = false
                    refreshButton.isVisible = false

                    changeVisibilityOfSearchHistoryElements(false)
                } else if ((case.listOfFoundedTracks[0].trackId.toInt() == -1)) {
                    progressBarTrackListLoading.isVisible = false
                    searchTracksRecycler.isVisible = false

                    changeParamsOfSearchEditText(false)

                    errorNoInternetText.isVisible = true
                    errorNothingFoundText.isVisible = false
                    noInternetPlaceholder.isVisible = true
                    noResultsPlaceholder.isVisible = false
                    refreshButton.isVisible = true

                    changeVisibilityOfSearchHistoryElements(false)
                } else {
                    progressBarTrackListLoading.isVisible = false
                    searchTracksRecycler.isVisible = true

                    changeParamsOfSearchEditText(true)

                    errorNoInternetText.isVisible = false
                    errorNothingFoundText.isVisible = false
                    noInternetPlaceholder.isVisible = false
                    noResultsPlaceholder.isVisible = false
                    refreshButton.isVisible = false

                    changeVisibilityOfSearchHistoryElements(false)
                }

            }
            is SearchScreenState.Loading -> {
                progressBarTrackListLoading.isVisible = true
                searchTracksRecycler.isVisible = false

                changeParamsOfSearchEditText(true)

                errorNoInternetText.isVisible = false
                errorNothingFoundText.isVisible = false
                noInternetPlaceholder.isVisible = false
                noResultsPlaceholder.isVisible = false
                refreshButton.isVisible = false

                changeVisibilityOfSearchHistoryElements(false)
            }
        }
    }

    //Function for changing visibility of all elements for displaying search history:
    //text "You looked", recycler view and button "Clear history"
    private fun changeVisibilityOfSearchHistoryElements(visible: Boolean) {
        clearHistoryButton.isVisible = visible
        searchHistoryRecycler.isVisible = visible
        youLookedForText.isVisible = visible
    }

    private fun changeParamsOfSearchEditText(isEditTextAvailable: Boolean) {
        searchEditText.isFocusableInTouchMode = isEditTextAvailable
        clearSearchEditTextButton.isEnabled = isEditTextAvailable
        clearSearchEditTextButton.isClickable = isEditTextAvailable
    }

    private fun searchDebounce() {
        mainHandler.removeCallbacksAndMessages(null)
        if (searchEditText.text?.isNotEmpty() == true)
            mainHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun tapDebounce() {
        isNotPressed = false
        mainHandler.postDelayed(tapEnableRunnable, TAP_DEBOUNCE_DELAY)
    }

    private fun changeStateOfScreenToContent(listOfTracks: List<Track>) {
        viewModel.setContentStateOfScreen(listOfTracks)
    }
}