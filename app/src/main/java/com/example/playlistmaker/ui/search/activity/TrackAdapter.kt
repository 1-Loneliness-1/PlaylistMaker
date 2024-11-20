package com.example.playlistmaker.ui.search.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.SearchListItemBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.utils.DimenConvertor

class TrackAdapter(
    private var tracks: List<Track> = emptyList(),
    private val onItemClicked: (Track) -> Unit,
    private val onLongItemClicked: (Track) -> Boolean
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(SearchListItemBinding.inflate(layoutInspector, parent, false)) {
            onItemClicked(tracks[it])
        }
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnLongClickListener {
            onLongItemClicked(tracks[position])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listOfTracks: List<Track>) {
        tracks = listOfTracks
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAllData() {
        tracks = emptyList()
        notifyDataSetChanged()
    }

    class TrackViewHolder(
        private val binding: SearchListItemBinding,
        onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bind(model: Track) {
            Glide.with(binding.root)
                .load(model.artworkUrl100)
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
                .into(binding.ivTrackCover)
            binding.tvTrackName.text = model.trackName
            binding.tvArtistName.text = model.artistName
            binding.tvTrackTime.text = model.trackTimeMillis
        }
    }

    companion object {
        private const val NUMBER_OF_DP_FOR_ROUNDING_CORNERS = 2f
    }

}