package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.ui.media.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistBinding

    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    companion object {
        fun newInstance() = PlaylistFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bCreateNewPlaylist.setOnClickListener {
            //To do something in the future lessons
        }
    }

}