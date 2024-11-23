package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.ui.media.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.utils.DimenConvertor
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class NewPlaylistFragment : Fragment() {

    private var isImageChanged = false
    private var _binding: FragmentNewPlaylistBinding? = null

    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBackToPrevScreen.setOnClickListener {
            showDialogOnClosingFragment()
        }

        binding.etTitleOfPlaylist.doAfterTextChanged { s ->
            binding.bSavePlaylist.isEnabled = s?.isNotEmpty() == true
        }

        val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                isImageChanged = true
                viewModel.saveUriOfPlaylistCover(uri)
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .transform(
                        RoundedCorners(
                            DimenConvertor.dpToPx(
                                NUMBER_OF_DP_FOR_IMAGE_CORNERS_ROUNDING,
                                requireContext()
                            )
                        )
                    )
                    .into(binding.ivAddPlaylistCover)
            }
        }

        binding.ivAddPlaylistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        binding.bSavePlaylist.setOnClickListener {
            val playlistTitle = binding.etTitleOfPlaylist.text.toString()
            val playlistDescription =
                if (binding.etPlaylistDescription.text.isEmpty() || binding.etPlaylistDescription.text.isBlank()) {
                    null
                } else {
                    binding.etPlaylistDescription.text.toString()
                }

            viewModel.savePlaylistInDatabase(playlistTitle, playlistDescription)

            Toast.makeText(
                requireContext(),
                requireContext().getString(
                    R.string.playlist_was_created,
                    binding.etTitleOfPlaylist.text
                ),
                Toast.LENGTH_LONG
            ).show()
            activity?.findViewById<FragmentContainerView>(R.id.fcvPlayerActivity)?.isVisible = false
            activity?.supportFragmentManager?.popBackStack()
        }

        val bundle = this.arguments
        if (bundle != null) {
            val currentPlaylistId = bundle.getLong(KEY_FOR_BUNDLE_DATA)

            binding.tvNewPlaylistHeader.text = getString(R.string.edit)

            viewModel.getEditPlaylistScreenStatusLiveData()
                .observe(viewLifecycleOwner) { editPlaylistScreenState ->
                    if (editPlaylistScreenState.currentPlaylist.playlistCoverPath != null) {
                        val imgFile =
                            File(editPlaylistScreenState.currentPlaylist.playlistCoverPath)
                        Glide.with(requireContext())
                            .load(imgFile)
                            .placeholder(R.drawable.song_cover_placeholder)
                            .centerCrop()
                            .transform(
                                RoundedCorners(
                                    DimenConvertor.dpToPx(
                                        NUMBER_OF_DP_FOR_IMAGE_CORNERS_ROUNDING,
                                        requireContext()
                                    )
                                )
                            )
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(binding.ivAddPlaylistCover)

                    }

                    binding.etTitleOfPlaylist.setText(editPlaylistScreenState.currentPlaylist.playlistTitle)

                    if (editPlaylistScreenState.currentPlaylist.playlistDescription != null) {
                        binding.etPlaylistDescription.setText(editPlaylistScreenState.currentPlaylist.playlistDescription)
                    }
                }

            binding.bSavePlaylist.apply {
                text = getString(R.string.save)

                setOnClickListener {
                    val updatedPlaylistTitle = binding.etTitleOfPlaylist.text.toString()
                    val updatedPlaylistDescription =
                        if (binding.etPlaylistDescription.text.isEmpty() || binding.etPlaylistDescription.text.isBlank()) {
                            null
                        } else {
                            binding.etPlaylistDescription.text.toString()
                        }

                    viewModel.updatePlaylist(updatedPlaylistTitle, updatedPlaylistDescription)

                    activity?.supportFragmentManager?.popBackStack()
                }
            }

            binding.ivBackToPrevScreen.setOnClickListener {
                activity?.supportFragmentManager?.popBackStack()
            }

            requireActivity().onBackPressedDispatcher.addCallback(this) {
                activity?.supportFragmentManager?.popBackStack()
            }

            viewModel.getInfoAboutPlaylistById(currentPlaylistId)
        } else {
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                showDialogOnClosingFragment()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (activity is PlayerActivity) {
            activity?.findViewById<BottomNavigationView>(R.id.bnvOnHostActivity)?.isVisible = true
            (activity as PlayerActivity).updateDataInPlaylistsRecyclerView()
        }
    }

    private fun showDialogOnClosingFragment() {
        if (binding.etTitleOfPlaylist.text.isNotEmpty() ||
            binding.etPlaylistDescription.text.isNotEmpty() ||
            isImageChanged
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(requireContext().getString(R.string.finalize_the_playlist))
                .setMessage(requireContext().getString(R.string.warning_of_unsaved_data_loss))
                .setNegativeButton(requireContext().getString(R.string.cancel)) { _, _ ->

                }
                .setPositiveButton(requireContext().getString(R.string.finalize)) { _, _ ->
                    activity?.supportFragmentManager?.popBackStack()
                    activity?.findViewById<FragmentContainerView>(R.id.fcvPlayerActivity)?.isVisible =
                        false
                }
                .show()
        } else {
            activity?.supportFragmentManager?.popBackStack()
            activity?.findViewById<FragmentContainerView>(R.id.fcvPlayerActivity)?.isVisible = false
        }
    }

    companion object {
        private const val NUMBER_OF_DP_FOR_IMAGE_CORNERS_ROUNDING = 8f
        private const val DEFAULT_VALUE_OF_PLAYLIST_ID = -1L
        private const val KEY_FOR_BUNDLE_DATA = "saved_playlist_id"

        fun newInstance(selectedPlaylistId: Long = DEFAULT_VALUE_OF_PLAYLIST_ID): NewPlaylistFragment {
            val fragment = NewPlaylistFragment()
            if (selectedPlaylistId != DEFAULT_VALUE_OF_PLAYLIST_ID) {
                val bundle = Bundle()
                bundle.putLong(KEY_FOR_BUNDLE_DATA, selectedPlaylistId)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

}