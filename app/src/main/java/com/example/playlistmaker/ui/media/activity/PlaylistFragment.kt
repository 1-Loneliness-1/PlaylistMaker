package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.media.model.PlaylistScreenState
import com.example.playlistmaker.ui.media.view_model.PlaylistsViewModel
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

        adapter = PlaylistAdapter {
            //TO do something by click on playlist element
        }
        binding.rvPlaylists.adapter = adapter

        viewModel.getPlaylistScreenStateLiveData()
            .observe(viewLifecycleOwner) { playlistScreenState ->
                when (playlistScreenState) {
                    is PlaylistScreenState.EmptyState -> {
                        adapter?.deleteAllData()
                        binding.ivPlaylistsNotFoundPlaceholder.visibility = View.VISIBLE
                        binding.tvPlaylistsNotCreatedYet.visibility = View.VISIBLE
                        binding.rvPlaylists.visibility = View.GONE
                    }

                    is PlaylistScreenState.ContentState -> {
                        adapter?.setData(playlistScreenState.playlists)
                        binding.ivPlaylistsNotFoundPlaceholder.visibility = View.GONE
                        binding.tvPlaylistsNotCreatedYet.visibility = View.GONE
                        binding.rvPlaylists.visibility = View.VISIBLE
                    }
                }
            }

        viewModel.getAllPlaylistsFromDatabase()

        binding.bCreateNewPlaylist.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bnvOnHostActivity).visibility =
                View.GONE

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