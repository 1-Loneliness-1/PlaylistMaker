package com.example.playlistmaker.ui.playlist.activity

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.domain.playlist.model.BottomSheetTrackListState
import com.example.playlistmaker.domain.playlist.model.PlaylistInfoScreenState
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
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoFragment : Fragment() {

    private var _binding: FragmentPlaylistInfoBinding? = null
    private var tracksInPlaylistAdapter: TrackAdapter? = null
    private var messageForShare = ""
    private var currentPlaylistId = 0L

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
                when (playlistInfoScreenState) {
                    is PlaylistInfoScreenState.ContentState -> {
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
                            binding.tvInfoPlaylistDescription.visibility = View.GONE
                        } else {
                            binding.tvInfoPlaylistDescription.visibility = View.VISIBLE
                            binding.tvInfoPlaylistDescription.text =
                                currentPlaylist.playlistDescription
                        }

                        binding.tvCountOfTracksInPlaylist.text =
                            currentPlaylist.trackCountInPlaylist.toString().plus(
                                if (currentPlaylist.trackCountInPlaylist % 10 == 1) " трек"
                                else if (currentPlaylist.trackCountInPlaylist % 10 in 2..4) " трека"
                                else " треков"
                            )
                        binding.tvTrackCountForMenu.text = binding.tvCountOfTracksInPlaylist.text
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

                        if (bottomSheetTrackListState.tracksInPlaylist.isEmpty()) {
                            binding.ivNoTracksInPlaylistPlaceholder.visibility = View.VISIBLE
                            binding.tvNoTracksInPlaylistText.visibility = View.VISIBLE
                            binding.rvTracksInPlaylist.visibility = View.GONE
                        } else {
                            binding.ivNoTracksInPlaylistPlaceholder.visibility = View.GONE
                            binding.tvNoTracksInPlaylistText.visibility = View.GONE
                            binding.rvTracksInPlaylist.visibility = View.VISIBLE
                        }

                        tracksInPlaylistAdapter?.setData(tracksInPlaylist)

                        messageForShare = ""

                        tracksInPlaylist.forEachIndexed { index, track ->
                            val trackDurationInMinAndSec =
                                oldDateFormat.parse(track.trackTimeMillis)
                            val trackDurationInMin =
                                targetDateFormat.format(trackDurationInMinAndSec!!)
                            playlistDuration += trackDurationInMin.toInt()

                            messageForShare =
                                messageForShare.plus("\n${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis})")
                        }

                        binding.tvCountOfTracksInPlaylist.text =
                            tracksInPlaylist.size.toString().plus(
                                if (tracksInPlaylist.size % 10 == 1) " трек"
                                else if (tracksInPlaylist.size % 10 in 2..4) " трека"
                                else " треков"
                            )

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
            currentPlaylistId = bundle.getLong(KEY_FOR_BUNDLE_DATA)
            viewModel.getPlaylistInfoById(currentPlaylistId)
        }

        val onItemClickListener: (Track) -> Unit = {
            val playerIntent = Intent(requireContext(), PlayerActivity::class.java)
            playerIntent.putExtra(KEY_FOR_INTENT_DATA, Gson().toJson(it))
            startActivity(playerIntent)
        }
        val onLongItemClickListener: (Track) -> Boolean = { selectedTrack ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Удалить трек")
                .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
                .setNegativeButton("Отмена") { _, _ ->

                }
                .setPositiveButton("Удалить") { _, _ ->
                    viewModel.deleteTrackFromPlaylist(currentPlaylistId, selectedTrack)
                }
                .show()

            true
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
                .setTitle("Удалить плейлист")
                .setMessage("Хотите удалить плейлист «${binding.tvPlaylistTitleForMenu.text}»?")
                .setNegativeButton("Нет") { _, _ ->

                }
                .setPositiveButton("Да") { _, _ ->
                    viewModel.deletePlaylistById(currentPlaylistId)
                    activity?.findViewById<BottomNavigationView>(R.id.bnvOnHostActivity)?.visibility =
                        View.VISIBLE
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
                "В этом плейлисте нет списка треков, которым можно поделиться",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val sharePlaylistMessage = "${binding.tvInfoPlaylistTitle.text}\n" +
                    "${if (binding.tvInfoPlaylistDescription.visibility == View.GONE) "" else binding.tvInfoPlaylistDescription.text}\n" +
                    "${binding.tvCountOfTracksInPlaylist.text}" + messageForShare

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, sharePlaylistMessage)
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