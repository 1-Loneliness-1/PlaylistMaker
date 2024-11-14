package com.example.playlistmaker.ui.media.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistsListItemBinding
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.utils.DimenConvertor
import java.io.File

class PlaylistAdapter(
    private var playlists: List<Playlist> = emptyList(),
    private val onItemClicked: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(
            PlaylistsListItemBinding.inflate(
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

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
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

    class PlaylistViewHolder(
        private val binding: PlaylistsListItemBinding,
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
            binding.tvPlaylistTitle.text = model.playlistTitle
            binding.tvNumOfTracksInPlaylist.text = model.trackCountInPlaylist.toString()
        }

    }

    companion object {
        private const val NUMBER_OF_DP_FOR_ROUNDING_CORNERS = 8f
    }

}