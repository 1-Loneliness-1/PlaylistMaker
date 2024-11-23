package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.model.SearchScreenState
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var isNotPressed = true
    private var searchEditText: EditText? = null
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
    private var listener: () -> Unit = {}
    private var adapter: TrackAdapter? = null
    private var searchHistoryAdapter: TrackAdapter? = null
    private var _binding: FragmentSearchBinding? = null
    private var searchJob: Job? = null
    private var previousTextInEditText: String = ""

    private val viewModel: SearchViewModel by viewModel()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val onItemClickListener: (Track) -> Unit = {
            if (isNotPressed) {
                tapDebounce()
                viewModel.saveNewTrack(it)

                val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
                playerIntent.putExtra(KEY_FOR_INTENT_DATA, Gson().toJson(it))
                startActivity(playerIntent)
            }
        }
        val onItemLongClick: (Track) -> Unit = { }
        adapter = TrackAdapter(
            onItemClicked = onItemClickListener,
            onLongItemClicked = onItemLongClick
        )
        searchTracksRecycler?.adapter = adapter

        val onItemClickListenerForSearchHistoryList: (Track) -> Unit = {
            if (isNotPressed) {
                tapDebounce()
                viewModel.saveNewTrack(it)
                viewModel.setWaitingStateForScreen()

                val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
                playerIntent.putExtra(KEY_FOR_INTENT_DATA, Gson().toJson(it))
                startActivity(playerIntent)
            }
        }
        searchHistoryAdapter = TrackAdapter(
            onItemClicked = onItemClickListenerForSearchHistoryList,
            onLongItemClicked = onItemLongClick
        )
        searchHistoryRecycler?.adapter = searchHistoryAdapter

        viewModel.getSearchScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is SearchScreenState.Waiting -> {
                    searchHistoryAdapter?.setData(screenState.tracksInSearchHistory)
                    changeVisibilityOfElements(screenState)
                }
                is SearchScreenState.Loading -> {
                    changeVisibilityOfElements(screenState)
                }
                is SearchScreenState.Content -> {
                    changeVisibilityOfElements(screenState)
                    if (screenState.listOfFoundedTracks.isNotEmpty() && screenState.listOfFoundedTracks[0].trackId.toInt() != BAD_REQUEST_TRACK_ID) {
                        adapter?.setData(screenState.listOfFoundedTracks)
                    }
                }
            }
        }

        refreshButton?.setOnClickListener {
            viewModel.getTracksForList(searchEditText?.text.toString())
        }

        clearSearchEditTextButton?.setOnClickListener {
            searchJob?.cancel()
            searchEditText?.setText("")
            clearSearchEditTextButton?.isVisible = false
            searchEditText?.clearFocus()
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)

            viewModel.setWaitingStateForScreen()

            adapter?.removeAllData()
        }

        searchEditText?.doAfterTextChanged { s ->
            clearSearchEditTextButton?.isVisible = s?.isNotEmpty() == true

            if (s?.isNotEmpty() == true) {
                searchDebounce()
            } else {
                searchJob?.cancel()
                viewModel.setWaitingStateForScreen()
            }
        }
        searchEditText?.setOnFocusChangeListener { _, hasFocus ->
            val conditionOfSearchEditText = hasFocus &&
                    searchEditText?.text?.isEmpty() == true &&
                    searchHistoryAdapter?.itemCount!! > 0
            changeVisibilityOfSearchHistoryElements(conditionOfSearchEditText)
        }

        clearHistoryButton?.setOnClickListener {
            viewModel.removeAllTracks()
            searchHistoryAdapter?.removeAllData()
            changeVisibilityOfSearchHistoryElements(false)
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
    }

    private fun changeVisibilityOfElements(case: SearchScreenState) {

        when (case) {
            is SearchScreenState.Waiting -> {
                progressBarTrackListLoading?.isVisible = false
                searchTracksRecycler?.isVisible = false

                changeParamsOfSearchEditText(true)

                errorNoInternetText?.isVisible = false
                errorNothingFoundText?.isVisible = false
                noInternetPlaceholder?.isVisible = false
                noResultsPlaceholder?.isVisible = false
                refreshButton?.isVisible = false

                changeVisibilityOfSearchHistoryElements(searchEditText?.text?.isEmpty() == true && searchHistoryAdapter?.itemCount!! > 0)
            }
            is SearchScreenState.Content -> {

                if (case.listOfFoundedTracks.isEmpty()) {
                    progressBarTrackListLoading?.isVisible = false
                    searchTracksRecycler?.isVisible = false

                    changeParamsOfSearchEditText(true)

                    errorNoInternetText?.isVisible = false
                    noInternetPlaceholder?.isVisible = false
                    refreshButton?.isVisible = false

                    changeVisibilityOfSearchHistoryElements(false)
                } else if ((case.listOfFoundedTracks[0].trackId.toInt() == BAD_REQUEST_TRACK_ID)) {
                    progressBarTrackListLoading?.isVisible = false
                    searchTracksRecycler?.isVisible = false

                    changeParamsOfSearchEditText(false)

                    errorNoInternetText?.isVisible = true
                    errorNothingFoundText?.isVisible = false
                    noInternetPlaceholder?.isVisible = true
                    noResultsPlaceholder?.isVisible = false
                    refreshButton?.isVisible = true

                    changeVisibilityOfSearchHistoryElements(false)
                } else {
                    progressBarTrackListLoading?.isVisible = false
                    searchTracksRecycler?.isVisible = true

                    changeParamsOfSearchEditText(true)

                    errorNoInternetText?.isVisible = false
                    errorNothingFoundText?.isVisible = false
                    noInternetPlaceholder?.isVisible = false
                    noResultsPlaceholder?.isVisible = false
                    refreshButton?.isVisible = false

                    changeVisibilityOfSearchHistoryElements(false)
                }

            }
            is SearchScreenState.Loading -> {
                progressBarTrackListLoading?.isVisible = true
                searchTracksRecycler?.isVisible = false

                changeParamsOfSearchEditText(true)

                errorNoInternetText?.isVisible = false
                errorNothingFoundText?.isVisible = false
                noInternetPlaceholder?.isVisible = false
                noResultsPlaceholder?.isVisible = false
                refreshButton?.isVisible = false

                changeVisibilityOfSearchHistoryElements(false)
            }
        }
    }

    private fun changeVisibilityOfSearchHistoryElements(visible: Boolean) {
        clearHistoryButton?.isVisible = visible
        searchHistoryRecycler?.isVisible = visible
        youLookedForText?.isVisible = visible
    }

    private fun changeParamsOfSearchEditText(isEditTextAvailable: Boolean) {
        searchEditText?.isFocusableInTouchMode = isEditTextAvailable
        clearSearchEditTextButton?.isEnabled = isEditTextAvailable
        clearSearchEditTextButton?.isClickable = isEditTextAvailable
    }

    private fun searchDebounce() {
        searchJob?.cancel()
        if (searchEditText?.text?.isNotEmpty() == true && searchEditText?.text?.toString() != previousTextInEditText)
            previousTextInEditText = searchEditText?.text?.toString()!!
        searchJob = lifecycleScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_IN_MILLISEC)
            viewModel.getTracksForList(searchEditText?.text.toString())
        }
    }

    private fun tapDebounce() {
        isNotPressed = false
        lifecycleScope.launch {
            delay(TAP_DEBOUNCE_DELAY_IN_MILLISEC)
            isNotPressed = true
        }
    }

    companion object {
        private const val KEY_FOR_INTENT_DATA = "Selected track"
        private const val SEARCH_DEBOUNCE_DELAY_IN_MILLISEC = 2000L
        private const val TAP_DEBOUNCE_DELAY_IN_MILLISEC = 1000L
        private const val BAD_REQUEST_TRACK_ID = -1
    }

}