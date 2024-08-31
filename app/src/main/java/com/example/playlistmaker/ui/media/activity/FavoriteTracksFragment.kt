package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.ui.media.view_model.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteTracksBinding

    private val favoriteTracksViewModel: FavoriteTracksViewModel by viewModel()

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

}