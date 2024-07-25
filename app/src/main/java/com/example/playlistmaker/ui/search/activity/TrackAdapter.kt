package com.example.playlistmaker.ui.search.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.SearchListItemBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.utils.DimenConvertor
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private val tracks: List<Track>,
    private val onItemClicked: (Track) -> Unit
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
    }

    class TrackViewHolder(
        private val binding: SearchListItemBinding,
        onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bind(model: Track) {
            Glide.with(itemView)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.song_cover_placeholder)
                .centerCrop()
                .transform(RoundedCorners(DimenConvertor.dpToPx(2f, itemView.context)))
                .into(binding.ivTrackCover)
            binding.tvTrackName.text = model.trackName
            binding.tvArtistName.text = model.artistName
            binding.tvTrackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        }
    }

}