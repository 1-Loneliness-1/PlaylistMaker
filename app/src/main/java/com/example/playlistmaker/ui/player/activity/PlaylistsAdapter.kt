package com.example.playlistmaker.ui.player.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistsBottomSheetListItemBinding
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.utils.DimenConvertor
import java.io.File

class PlaylistsAdapter(
    private var playlists: List<Playlist> = emptyList(),
    private val onItemClicked: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsAdapter.PlaylistsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistsViewHolder(
            PlaylistsBottomSheetListItemBinding.inflate(
                layoutInspector,
                parent,
                false
            )
        ) {
            onItemClicked(playlists[it])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listOfPlaylists: List<Playlist>) {
        playlists = listOfPlaylists
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAllData() {
        playlists = emptyList()
        notifyDataSetChanged()
    }

    class PlaylistsViewHolder(
        private val binding: PlaylistsBottomSheetListItemBinding,
        onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bind(model: Playlist) {
            if (model.playlistCoverPath != null) {
                val imgFile = File(model.playlistCoverPath)
                Glide.with(binding.root)
                    .load(imgFile)
                    .placeholder(R.drawable.song_cover_placeholder)
                    .centerCrop()
                    .transform(
                        RoundedCorners(
                            DimenConvertor.dpToPx(
                                NUMBER_OF_DP_FOR_ROUNDING_CORNERS,
                                binding.root.context
                            )
                        )
                    )
                    .into(binding.ivPlaylistCover)
            } else {
                binding.ivPlaylistCover.setImageResource(R.drawable.song_cover_placeholder)
            }
            binding.tvTitleOfPlaylist.text = model.playlistTitle
            binding.tvNumOfTracks.text = model.trackCountInPlaylist.toString().plus(
                if (model.trackCountInPlaylist % 10 == 1) " трек"
                else if (model.trackCountInPlaylist % 10 in 2..4) " трека"
                else " треков"
            )

        }
    }

    companion object {
        private const val NUMBER_OF_DP_FOR_ROUNDING_CORNERS = 2f
    }
}