package com.example.playlistmaker.ui.media.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.ui.media.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.utils.DimenConvertor
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    private var uriOfImageForSave: Uri? = null
    private var _binding: FragmentNewPlaylistBinding? = null

    private val binding get() = _binding!!
    private val viewModel: NewPlaylistViewModel by viewModel()
    private var defaultImage: Drawable? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        defaultImage = binding.ivAddPlaylistCover.drawable

        binding.ivBackToPrevScreen.setOnClickListener {
            showDialogOnClosingFragment()
        }

        binding.etTitleOfPlaylist.doAfterTextChanged { s ->
            binding.bSavePlaylist.isEnabled = s?.isNotEmpty() == true
        }

        val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                uriOfImageForSave = uri
                binding.ivAddPlaylistCover.setImageURI(uri)
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

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            showDialogOnClosingFragment()
        }

        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "PlaylistsCovers"
        )

        binding.bSavePlaylist.setOnClickListener {
            if (!filePath.exists()) {
                filePath.mkdirs()
            }
            val file = File(filePath, "${binding.etTitleOfPlaylist.text}_cover.jpg")
            if (uriOfImageForSave != null) {
                val inputStream =
                    requireContext().contentResolver.openInputStream(uriOfImageForSave!!)
                val outputStream = FileOutputStream(file)
                BitmapFactory
                    .decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }

            viewModel.savePlaylistInDatabase(
                Playlist(
                    PLAYLIST_ID_PLACEHOLDER,
                    binding.etTitleOfPlaylist.text.toString(),
                    if (binding.etPlaylistDescription.text.isEmpty() || binding.etPlaylistDescription.text.isBlank()) {
                        null
                    } else {
                        binding.etPlaylistDescription.text.toString()
                    },
                    if (binding.ivAddPlaylistCover.drawable == defaultImage) {
                        null
                    } else {
                        file.path
                    },
                    null,
                    INIT_NUMBER_OF_TRACKS_IN_PLAYLIST
                )
            )

            Toast.makeText(
                requireContext(),
                "Плейлист ${binding.etTitleOfPlaylist.text} создан",
                Toast.LENGTH_LONG
            ).show()
            activity?.findViewById<FragmentContainerView>(R.id.fcvPlayerActivity)?.visibility =
                View.GONE
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<BottomNavigationView>(R.id.bnvOnHostActivity)?.visibility =
            View.VISIBLE
        if (activity is PlayerActivity) {
            (activity as PlayerActivity).updateDataInPlaylistsRecyclerView()
        }
    }

    private fun showDialogOnClosingFragment() {
        if (binding.etTitleOfPlaylist.text.isNotEmpty() ||
            binding.etPlaylistDescription.text.isNotEmpty() ||
            binding.ivAddPlaylistCover.drawable != defaultImage
        ) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setMessage("Все несохраненные данные будут потеряны")
                .setNegativeButton("Отмена") { _, _ ->

                }
                .setPositiveButton("Завершить") { _, _ ->
                    activity?.supportFragmentManager?.popBackStack()
                    activity?.findViewById<FragmentContainerView>(R.id.fcvPlayerActivity)?.visibility =
                        View.GONE
                }
                .show()
        } else {
            activity?.supportFragmentManager?.popBackStack()
            activity?.findViewById<FragmentContainerView>(R.id.fcvPlayerActivity)?.visibility =
                View.GONE
        }
    }

    companion object {
        private const val NUMBER_OF_DP_FOR_IMAGE_CORNERS_ROUNDING = 8f
        private const val PLAYLIST_ID_PLACEHOLDER = 0L
        private const val INIT_NUMBER_OF_TRACKS_IN_PLAYLIST = 0

        fun newInstance() = NewPlaylistFragment()
    }

}