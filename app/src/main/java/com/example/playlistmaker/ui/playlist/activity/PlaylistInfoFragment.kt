package com.example.playlistmaker.ui.playlist.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.media.activity.NewPlaylistFragment
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.ui.playlist.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.ui.search.activity.TrackAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentPlaylistInfoBinding? = null
    private var tracksInPlaylistAdapter: TrackAdapter? = null
    private var currentPlaylistId = 0L
    private var messageForShare = ""

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
            activity?.findViewById<BottomNavigationView>(R.id.bnvOnHostActivity)?.isVisible = true
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.ivBackToPreviousScreen.setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bnvOnHostActivity)?.isVisible = true
            activity?.supportFragmentManager?.popBackStack()
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.llTracksInPlaylistBottomSheet)
        val menuBottomSheetBehavior = BottomSheetBehavior.from(binding.clMenuBottomSheet)

        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.clPlaylistInfo.viewTreeObserver.addOnGlobalLayoutListener {
            bottomSheetBehavior.apply {
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    val locationOfLastElement = IntArray(2)
                    binding.ibSharePlaylist.getLocationOnScreen(locationOfLastElement)

                    peekHeight =
                        Resources.getSystem().displayMetrics.heightPixels - locationOfLastElement[1] -
                                binding.ibSharePlaylist.layoutParams.height - 32
                }
            }

            menuBottomSheetBehavior.apply {
                peekHeight =
                    binding.clMenuBottomSheet.getChildAt(6).bottom + EXTRA_MARGIN_FOR_MENU_BOTTOM_SHEET_IN_DP
            }
        }

        viewModel.getPlaylistInfoScreenStatusLiveData()
            .observe(viewLifecycleOwner) { playlistInfoScreenState ->
                val currentPlaylist = playlistInfoScreenState.playlist

                if (currentPlaylist.playlistCoverPath != null) {
                    val fileWithPlaylistCover = File(currentPlaylist.playlistCoverPath)
                    Glide.with(this)
                        .load(fileWithPlaylistCover)
                        .placeholder(R.drawable.song_cover_placeholder)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.ivInfoPlaylistCover)
                    Glide.with(this)
                        .load(fileWithPlaylistCover)
                        .placeholder(R.drawable.song_cover_placeholder)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.ivPlaylistCoverForMenu)
                } else {
                    binding.ivInfoPlaylistCover.setImageResource(R.drawable.song_cover_placeholder)
                    binding.ivPlaylistCoverForMenu.setImageResource(R.drawable.song_cover_placeholder)
                }

                binding.tvInfoPlaylistTitle.text = currentPlaylist.playlistTitle
                binding.tvPlaylistTitleForMenu.text = currentPlaylist.playlistTitle

                if (currentPlaylist.playlistDescription == null) {
                    binding.tvInfoPlaylistDescription.isVisible = false
                } else {
                    binding.tvInfoPlaylistDescription.isVisible = true
                    binding.tvInfoPlaylistDescription.text = currentPlaylist.playlistDescription
                }

                binding.tvCountOfTracksInPlaylist.text =
                    requireContext().resources.getQuantityString(
                        R.plurals.track_plurals,
                        currentPlaylist.trackCountInPlaylist,
                        currentPlaylist.trackCountInPlaylist
                    )
                binding.tvTrackCountForMenu.text = binding.tvCountOfTracksInPlaylist.text
            }

        viewModel.getBottomSheetTrackListStatusLiveData()
            .observe(viewLifecycleOwner) { bottomSheetTrackListState ->
                val tracksInPlaylist = bottomSheetTrackListState.tracksInPlaylist

                if (bottomSheetTrackListState.tracksInPlaylist.isEmpty()) {
                    binding.ivNoTracksInPlaylistPlaceholder.isVisible = true
                    binding.tvNoTracksInPlaylistText.isVisible = true
                    binding.rvTracksInPlaylist.isVisible = false
                } else {
                    binding.ivNoTracksInPlaylistPlaceholder.isVisible = false
                    binding.tvNoTracksInPlaylistText.isVisible = false
                    binding.rvTracksInPlaylist.isVisible = true
                }

                tracksInPlaylistAdapter?.setData(tracksInPlaylist)

                binding.tvCountOfTracksInPlaylist.text =
                    requireContext().resources.getQuantityString(
                        R.plurals.track_plurals,
                        tracksInPlaylist.size,
                        tracksInPlaylist.size
                    )

                binding.tvPlaylistDuration.text = bottomSheetTrackListState.playlistDuration
                messageForShare = bottomSheetTrackListState.messageForShare
            }

        val bundle = this.arguments
        if (bundle != null) {
            currentPlaylistId = bundle.getLong(KEY_FOR_BUNDLE_DATA)
            viewModel.getPlaylistInfoById(currentPlaylistId)
        }

        val onItemClickListener: (Track) -> Unit = {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(KEY_FOR_INTENT_DATA, Gson().toJson(it))
            startActivity(playerIntent)
        }
        val onLongItemClickListener: (Track) -> Unit = { selectedTrack ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.delete_track))
                .setMessage(requireContext().getString(R.string.delete_track_from_playlist_question))
                .setNegativeButton(requireContext().getString(R.string.cancel)) { _, _ ->

                }
                .setPositiveButton(requireContext().getString(R.string.delete)) { _, _ ->
                    viewModel.deleteTrackFromPlaylist(currentPlaylistId, selectedTrack)
                }
                .show()
        }

        tracksInPlaylistAdapter = TrackAdapter(
            onItemClicked = onItemClickListener,
            onLongItemClicked = onLongItemClickListener
        )
        binding.rvTracksInPlaylist.adapter = tracksInPlaylistAdapter

        binding.ibSharePlaylist.setOnClickListener {
            sharePlaylist()
        }

        binding.ibOpenMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.tvShareInMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.tvEditInformationAboutPlaylistInMenu.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fcvHostActivity,
                    NewPlaylistFragment.newInstance(currentPlaylistId),
                    null
                )
                .addToBackStack(null)
                .commit()
        }

        binding.tvDeletePlaylistInMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.delete_playlist))
                .setMessage(
                    requireContext().getString(
                        R.string.do_you_want_delete_playlist,
                        binding.tvPlaylistTitleForMenu.text
                    )
                )
                .setNegativeButton(requireContext().getString(R.string.no)) { _, _ ->

                }
                .setPositiveButton(requireContext().getString(R.string.yes)) { _, _ ->
                    viewModel.deletePlaylistById(currentPlaylistId)
                    activity?.findViewById<BottomNavigationView>(R.id.bnvOnHostActivity)?.isVisible =
                        true
                    activity?.supportFragmentManager?.popBackStack()
                }
                .show()
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylistInfoById(currentPlaylistId)
    }

    private fun sharePlaylist() {
        if (tracksInPlaylistAdapter?.itemCount == 0) {
            Toast.makeText(
                requireContext(),
                getString(R.string.empty_playlist_for_share),
                Toast.LENGTH_LONG
            ).show()
        } else {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, messageForShare)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_apk_text)))
        }
    }

    companion object {
        private const val KEY_FOR_BUNDLE_DATA = "selected_playlist"
        private const val KEY_FOR_INTENT_DATA = "Selected track"
        private const val EXTRA_MARGIN_FOR_MENU_BOTTOM_SHEET_IN_DP = 60

        fun newInstance(selectedPlaylistId: Long): PlaylistInfoFragment {
            val fragment = PlaylistInfoFragment()
            val bundle = Bundle()
            bundle.putLong(KEY_FOR_BUNDLE_DATA, selectedPlaylistId)
            fragment.arguments = bundle

            return fragment
        }
    }

}