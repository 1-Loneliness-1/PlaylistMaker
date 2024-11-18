package com.example.playlistmaker.ui.playlist.activity

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.domain.playlist.model.BottomSheetTrackListState
import com.example.playlistmaker.domain.playlist.model.PlaylistInfoScreenState
import com.example.playlistmaker.ui.playlist.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.ui.search.activity.TrackAdapter
import com.example.playlistmaker.utils.DimenConvertor
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentPlaylistInfoBinding? = null
    private var tracksInPlaylistAdapter: TrackAdapter? = null

    private val viewModel: PlaylistInfoViewModel by viewModel()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.findViewById<BottomNavigationView>(R.id.bnvOnHostActivity)?.visibility =
                View.VISIBLE
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.ivBackToPreviousScreen.setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bnvOnHostActivity)?.visibility =
                View.VISIBLE
            activity?.supportFragmentManager?.popBackStack()
        }

        tracksInPlaylistAdapter = TrackAdapter {

        }
        binding.rvTracksInPlaylist.adapter = tracksInPlaylistAdapter

        binding.clPlaylistInfo.viewTreeObserver.addOnGlobalLayoutListener {
            BottomSheetBehavior.from(binding.llTracksInPlaylistBottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED

                val locationOfLastElement = IntArray(2)
                binding.ibSharePlaylist.getLocationOnScreen(locationOfLastElement)

                peekHeight =
                    Resources.getSystem().displayMetrics.heightPixels - locationOfLastElement[1] -
                            binding.ibSharePlaylist.layoutParams.height - DimenConvertor.dpToPx(
                        MARGIN_TOP_IN_DP,
                        requireContext()
                    )
            }
        }

        viewModel.getPlaylistInfoScreenStatusLiveData()
            .observe(viewLifecycleOwner) { playlistInfoScreenState ->
                when (playlistInfoScreenState) {
                    is PlaylistInfoScreenState.ContentState -> {
                        val currentPlaylist = playlistInfoScreenState.playlist

                        if (currentPlaylist.playlistCoverPath != null) {
                            val fileWithPlaylistCover = File(currentPlaylist.playlistCoverPath)
                            Glide.with(this)
                                .load(fileWithPlaylistCover)
                                .placeholder(R.drawable.song_cover_placeholder)
                                .centerCrop()
                                .into(binding.ivInfoPlaylistCover)
                        } else {
                            binding.ivInfoPlaylistCover.setImageResource(R.drawable.song_cover_placeholder)
                        }

                        binding.tvInfoPlaylistTitle.text = currentPlaylist.playlistTitle

                        if (currentPlaylist.playlistDescription == null) {
                            binding.tvInfoPlaylistDescription.visibility = View.GONE
                        } else {
                            binding.tvInfoPlaylistDescription.text =
                                currentPlaylist.playlistDescription
                        }

                        binding.tvCountOfTracksInPlaylist.text =
                            currentPlaylist.trackCountInPlaylist.toString().plus(
                                if (currentPlaylist.trackCountInPlaylist % 10 == 1) " трек"
                                else if (currentPlaylist.trackCountInPlaylist % 10 in 2..4) " трека"
                                else " треков"
                            )

                    }
                }
            }

        viewModel.getBottomSheetTrackListStatusLiveData()
            .observe(viewLifecycleOwner) { bottomSheetTrackListState ->
                when (bottomSheetTrackListState) {
                    is BottomSheetTrackListState.ContentState -> {
                        val tracksInPlaylist = bottomSheetTrackListState.tracksInPlaylist
                        val oldDateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
                        val targetDateFormat = SimpleDateFormat("mm", Locale.getDefault())
                        var playlistDuration = 0

                        tracksInPlaylistAdapter?.setData(tracksInPlaylist)

                        tracksInPlaylist.forEach { track ->
                            val trackDurationInMinAndSec =
                                oldDateFormat.parse(track.trackTimeMillis)
                            val trackDurationInMin =
                                targetDateFormat.format(trackDurationInMinAndSec!!)
                            playlistDuration += trackDurationInMin.toInt()
                        }

                        binding.tvPlaylistDuration.text = playlistDuration.toString().plus(
                            if (playlistDuration % 100 == 1) " минута"
                            else if (playlistDuration % 100 in 2..4) " минуты"
                            else " минут"
                        )

                    }
                }
            }

        val bundle = this.arguments
        if (bundle != null) {
            val playlistId = bundle.getLong(KEY_FOR_BUNDLE_DATA)
            viewModel.getPlaylistInfoById(playlistId)
        }

    }

    companion object {

        private const val KEY_FOR_BUNDLE_DATA = "selected_playlist"
        private const val MARGIN_TOP_IN_DP = 24f

        fun newInstance(selectedPlaylistId: Long): PlaylistInfoFragment {
            val fragment = PlaylistInfoFragment()
            val bundle = Bundle()
            bundle.putLong(KEY_FOR_BUNDLE_DATA, selectedPlaylistId)
            fragment.arguments = bundle

            return fragment
        }
    }

}