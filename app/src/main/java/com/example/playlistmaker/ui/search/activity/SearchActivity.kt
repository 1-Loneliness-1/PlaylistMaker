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
import androidx.activity.viewModels
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
    private val viewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory(
            application,
            NAME_FOR_FILE_WITH_SEARCH_HISTORY,
            KEY_FOR_ARRAY_WITH_SEARCH_HISTORY
        )
    }

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
        const val NAME_FOR_FILE_WITH_SEARCH_HISTORY = "search_history"
        const val KEY_FOR_ARRAY_WITH_SEARCH_HISTORY = "elems_in_search_history"
        private const val KEY_FOR_INTENT_DATA = "Selected track"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val TAP_DEBOUNCE_DELAY = 1000L
        private val trackList = ArrayList<Track>()
        private val searchHistoryList = ArrayList<Track>()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSearchScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is SearchScreenState.Waiting -> {
                    //Hiding all elements on the screen
                    changeVisibilityOfElements(false, "Hide all")
                }
                is SearchScreenState.Loading -> {
                    //Showing progressbar? or something different
                    changeVisibilityOfElements(false)
                }
                is SearchScreenState.Content -> {
                    trackList.clear()
                    //Showing necessary elements and hiding progress bar
                    if (screenState.listOfFoundedTracks.isEmpty()) {
                        adapter.notifyDataSetChanged()
                        changeVisibilityOfElements(true, "Nothing found")
                    } else if (screenState.listOfFoundedTracks[0].trackId.toInt() == -1) {
                        adapter.notifyDataSetChanged()
                        changeVisibilityOfElements(true, "No internet")
                    } else {
                        trackList.addAll(screenState.listOfFoundedTracks)
                        adapter.notifyDataSetChanged()
                        changeVisibilityOfElements(true)
                    }
                }
            }
        }

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

        val searchHistory = SearchHistory(viewModel.sharPrefInteractor)

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
        searchTracksRecycler.adapter = adapter

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
        searchHistoryRecycler.adapter = searchHistoryAdapter

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

            trackList.clear()
            adapter.notifyDataSetChanged()
        }

        searchEditText.doAfterTextChanged { s ->
            clearSearchEditTextButton.isVisible = s?.isNotEmpty() == true

            //Saving current text in edit text in variable for putting in Instance State
            currentTextInEditText = s.toString()

            if (s?.isNotEmpty() == true) {
                searchDebounce()
            } else {
                mainHandler.removeCallbacksAndMessages(searchRunnable)
                viewModel.setWaitingStateForScreen()
            }
        }
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            val conditionOfSearchEditText = hasFocus &&
                    searchEditText.text?.isEmpty() == true &&
                    searchHistoryList.isNotEmpty()
            changeVisibilityOfSearchHistoryElements(conditionOfSearchEditText)
        }

        //Implementation of on click listener for clear search history button
        clearHistoryButton.setOnClickListener {
            searchHistory.removeAllTracks()
            searchHistoryList.clear()
            searchHistoryAdapter.notifyDataSetChanged()
            changeVisibilityOfSearchHistoryElements(false)
        }

        //Implementation of change listener for instance of shared preferences
        listener = {
            searchHistoryList.clear()
            searchHistoryList.addAll(searchHistory.tracksInSearchHistory)
            searchHistoryAdapter.notifyDataSetChanged()
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

    private fun changeVisibilityOfElements(visibility: Boolean, case: String = "Normally") {
        when (case) {
            "Hide all" -> {
                binding.pbListOfTracksLoading.isVisible = false
                binding.rvTrackSearchList.isVisible = false

                searchEditText.isFocusableInTouchMode = true
                clearSearchEditTextButton.isEnabled = true
                clearSearchEditTextButton.isClickable = true

                binding.tvErrorNoInternet.isVisible = false
                binding.tvErrorNothingFound.isVisible = false
                binding.ivNoInternetPlaceholder.isVisible = false
                binding.ivNothingFoundPlaceholder.isVisible = false
                binding.bRefreshRequest.isVisible = false

                changeVisibilityOfSearchHistoryElements(currentTextInEditText.isEmpty() && searchHistoryList.isNotEmpty())
            }
            "Normally" -> {
                binding.pbListOfTracksLoading.isVisible = !visibility
                binding.rvTrackSearchList.isVisible = visibility

                searchEditText.isFocusableInTouchMode = true
                clearSearchEditTextButton.isEnabled = true
                clearSearchEditTextButton.isClickable = true

                binding.tvErrorNoInternet.isVisible = false
                binding.tvErrorNothingFound.isVisible = false
                binding.ivNoInternetPlaceholder.isVisible = false
                binding.ivNothingFoundPlaceholder.isVisible = false
                binding.bRefreshRequest.isVisible = false

                changeVisibilityOfSearchHistoryElements(false)
            }
            "Nothing found" -> {
                binding.pbListOfTracksLoading.isVisible = false
                binding.rvTrackSearchList.isVisible = false

                searchEditText.isFocusableInTouchMode = true
                clearSearchEditTextButton.isEnabled = true
                clearSearchEditTextButton.isClickable = true

                binding.tvErrorNoInternet.isVisible = false
                binding.ivNoInternetPlaceholder.isVisible = false
                binding.bRefreshRequest.isVisible = false

                if (currentTextInEditText.isEmpty()) {
                    viewModel.setWaitingStateForScreen()
                }

                changeVisibilityOfSearchHistoryElements(false)
            }
            "No internet" -> {
                binding.pbListOfTracksLoading.isVisible = false
                binding.rvTrackSearchList.isVisible = false

                searchEditText.isFocusableInTouchMode = false
                clearSearchEditTextButton.isEnabled = false
                clearSearchEditTextButton.isClickable = false

                binding.tvErrorNoInternet.isVisible = true
                binding.tvErrorNothingFound.isVisible = false
                binding.ivNoInternetPlaceholder.isVisible = true
                binding.ivNothingFoundPlaceholder.isVisible = false
                binding.bRefreshRequest.isVisible = true

                changeVisibilityOfSearchHistoryElements(false)
            }
        }
    }

    //Function for changing visibility of all elements for displaying search history:
    //text "You looked", recycler view and button "Clear history"
    private fun changeVisibilityOfSearchHistoryElements(visible: Boolean) {
        binding.bClearSearchHistory.isVisible = visible
        binding.rvSearchHistory.isVisible = visible
        binding.tvYouLookedFor.isVisible = visible
    }

    private fun searchDebounce() {
        mainHandler.removeCallbacksAndMessages(searchRunnable)
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