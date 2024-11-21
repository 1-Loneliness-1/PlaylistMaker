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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.domain.media.model.EditPlaylistScreenState
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
    private var defaultImage: Drawable? = null
    private var oldTitleOfPlaylist = ""
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
            val fileWithCover = saveCoverOfPlaylist()

            viewModel.savePlaylistInDatabase(
                Playlist(
                    PLAYLIST_ID_PLACEHOLDER,
                    binding.etTitleOfPlaylist.text.toString(),
                    if (binding.etPlaylistDescription.text.isEmpty() || binding.etPlaylistDescription.text.isBlank()) {
                        null
                    } else {
                        binding.etPlaylistDescription.text.toString()
                    },
                    fileWithCover?.path,
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

        val bundle = this.arguments
        if (bundle != null) {
            val currentPlaylistId = bundle.getLong(KEY_FOR_BUNDLE_DATA)

            binding.tvNewPlaylistHeader.text = "Редактировать"

            viewModel.getEditPlaylistScreenStatusLiveData()
                .observe(viewLifecycleOwner) { editPlaylistScreenState ->
                    when (editPlaylistScreenState) {
                        is EditPlaylistScreenState.ContentState -> {
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
                            oldTitleOfPlaylist = binding.etTitleOfPlaylist.text.toString()

                            if (editPlaylistScreenState.currentPlaylist.playlistDescription != null) {
                                binding.etPlaylistDescription.setText(editPlaylistScreenState.currentPlaylist.playlistDescription)
                            }
                        }
                    }
                }

            binding.bSavePlaylist.apply {
                text = "Сохранить"

                setOnClickListener {
                    val updatedPlaylistTitle = binding.etTitleOfPlaylist.text.toString()
                    val updatedPlaylistDescription =
                        if (binding.etPlaylistDescription.text.isEmpty() || binding.etPlaylistDescription.text.isBlank()) {
                            null
                        } else {
                            binding.etPlaylistDescription.text.toString()
                        }

                    val imgFile = savePlaylistCoverInExternalMemory()
                    val updatedPlaylistCoverPath = imgFile?.path

                    viewModel.updatePlaylist(
                        currentPlaylistId,
                        updatedPlaylistTitle,
                        updatedPlaylistDescription,
                        updatedPlaylistCoverPath
                    )

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
            activity?.findViewById<BottomNavigationView>(R.id.bnvOnHostActivity)?.visibility =
                View.VISIBLE
            (activity as PlayerActivity).updateDataInPlaylistsRecyclerView()
        }
    }

    private fun savePlaylistCoverInExternalMemory(): File? {
        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "PlaylistsCovers"
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        if (oldTitleOfPlaylist != binding.etTitleOfPlaylist.text.toString()) {
            val oldFile = File(filePath, "${oldTitleOfPlaylist}_cover.jpg")

            if (oldFile.exists()) {
                if (uriOfImageForSave == null) {
                    val newFile = File(filePath, "${binding.etTitleOfPlaylist.text}_cover.jpg")
                    oldFile.copyTo(newFile)
                    oldFile.delete()

                    return newFile
                } else {
                    oldFile.delete()
                    val file = File(filePath, "${binding.etTitleOfPlaylist.text}_cover.jpg")
                    val inputStream =
                        requireContext().contentResolver.openInputStream(uriOfImageForSave!!)
                    val outputStream = FileOutputStream(file)
                    BitmapFactory
                        .decodeStream(inputStream)
                        .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

                    return file
                }
            } else {
                if (uriOfImageForSave != null) {
                    val file = File(filePath, "${binding.etTitleOfPlaylist.text}_cover.jpg")
                    val inputStream =
                        requireContext().contentResolver.openInputStream(uriOfImageForSave!!)
                    val outputStream = FileOutputStream(file)
                    BitmapFactory
                        .decodeStream(inputStream)
                        .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

                    return file
                } else {
                    return null
                }
            }

        } else {
            val fileWithCoverPath = File(filePath, "${oldTitleOfPlaylist}_cover.jpg")

            if (fileWithCoverPath.exists()) {
                if (uriOfImageForSave != null) {
                    val inputStream =
                        requireContext().contentResolver.openInputStream(uriOfImageForSave!!)
                    val outputStream = FileOutputStream(fileWithCoverPath)
                    BitmapFactory
                        .decodeStream(inputStream)
                        .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                }

                return fileWithCoverPath
            } else {
                if (uriOfImageForSave != null) {
                    val inputStream =
                        requireContext().contentResolver.openInputStream(uriOfImageForSave!!)
                    val outputStream = FileOutputStream(fileWithCoverPath)
                    BitmapFactory
                        .decodeStream(inputStream)
                        .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

                    return fileWithCoverPath
                } else {
                    return null
                }
            }
        }

    }

    private fun saveCoverOfPlaylist(): File? {
        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "PlaylistsCovers"
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        if (uriOfImageForSave != null) {
            val file = File(filePath, "${binding.etTitleOfPlaylist.text}_cover.jpg")
            val inputStream =
                requireContext().contentResolver.openInputStream(uriOfImageForSave!!)
            val outputStream = FileOutputStream(file)
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

            return file
        } else {
            return null
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