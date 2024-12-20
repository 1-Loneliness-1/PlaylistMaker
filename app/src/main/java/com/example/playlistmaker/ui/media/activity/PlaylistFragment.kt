package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.media.model.PlaylistScreenState
import com.example.playlistmaker.ui.media.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.playlist.activity.PlaylistInfoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private var adapter: PlaylistAdapter? = null

    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPlaylists.addItemDecoration(SpacesItemDecoration(requireContext()))

        adapter = PlaylistAdapter { selectedPlaylist ->
            requireActivity().findViewById<BottomNavigationView>(R.id.bnvOnHostActivity).isVisible =
                false

            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fcvHostActivity,
                    PlaylistInfoFragment.newInstance(selectedPlaylist.playlistId),
                    null
                )
                .addToBackStack(null)
                .commit()
        }
        binding.rvPlaylists.adapter = adapter

        viewModel.getPlaylistScreenStateLiveData()
            .observe(viewLifecycleOwner) { playlistScreenState ->
                when (playlistScreenState) {
                    is PlaylistScreenState.EmptyState -> {
                        adapter?.deleteAllData()
                        binding.ivPlaylistsNotFoundPlaceholder.isVisible = true
                        binding.tvPlaylistsNotCreatedYet.isVisible = true
                        binding.rvPlaylists.isVisible = false
                    }

                    is PlaylistScreenState.ContentState -> {
                        adapter?.setData(playlistScreenState.playlists)
                        binding.ivPlaylistsNotFoundPlaceholder.isVisible = false
                        binding.tvPlaylistsNotCreatedYet.isVisible = false
                        binding.rvPlaylists.isVisible = true
                    }
                }
            }

        viewModel.getAllPlaylistsFromDatabase()

        binding.bCreateNewPlaylist.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bnvOnHostActivity).isVisible =
                false

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fcvHostActivity, NewPlaylistFragment.newInstance(), null)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllPlaylistsFromDatabase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }

}