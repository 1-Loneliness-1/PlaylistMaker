package com.example.playlistmaker.ui.media.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.domain.media.model.FavoriteTracksScreenState
import com.example.playlistmaker.ui.media.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.ui.search.activity.TrackAdapter
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var binding: FragmentFavoriteTracksBinding? = null
    private var adapter: TrackAdapter? = null
    private var isNotPressed = true

    private val viewModel: FavoriteTracksViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favoriteTracksRecyclerView = binding?.rvFavoriteTracks
        val noFavoriteTracksPlaceholder: ImageView? = binding?.ivFavoriteTracksNotFoundPlaceholder
        val noFavoriteTracksText: TextView? = binding?.tvFavoriteTracksIsEmpty

        adapter = TrackAdapter {
            if (isNotPressed) {
                tapDebounce()

                val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
                playerIntent.putExtra(KEY_FOR_INTENT_DATA, Gson().toJson(it))
                startActivity(playerIntent)
            }
        }
        favoriteTracksRecyclerView?.adapter = adapter

        viewModel.getFavoriteTracksScreenStateLiveData()
            .observe(viewLifecycleOwner) { favoriteTracksScreenState ->
                when (favoriteTracksScreenState) {
                    is FavoriteTracksScreenState.EmptyState -> {
                        noFavoriteTracksPlaceholder?.isVisible = true
                        noFavoriteTracksText?.isVisible = true
                        favoriteTracksRecyclerView?.isVisible = false
                        adapter?.removeAllData()
                    }

                    is FavoriteTracksScreenState.WithContentState -> {
                        noFavoriteTracksPlaceholder?.isVisible = false
                        noFavoriteTracksText?.isVisible = false
                        adapter?.setData(favoriteTracksScreenState.favoriteTracks)
                        favoriteTracksRecyclerView?.isVisible = true
                    }
                }
            }

        viewModel.getFavoriteTracksFromBd()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getFavoriteTracksFromBd()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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
        private const val TAP_DEBOUNCE_DELAY_IN_MILLISEC = 1000L

        fun newInstance() = FavoriteTracksFragment()
    }

}