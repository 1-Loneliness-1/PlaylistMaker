package com.example.playlistmaker.presentation.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entities.Track
import com.example.playlistmaker.utils.DimenConvertor
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter(
    private val tracks: List<Track>,
    private val onItemClicked: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        return TrackViewHolder(viewHolder) {
            onItemClicked(tracks[it])
        }
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    class TrackViewHolder(
        itemView: View,
        onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val songCover = itemView.findViewById<ImageView>(R.id.ivTrackCover)
        private val trackName = itemView.findViewById<TextView>(R.id.tvTrackName)
        private val artistName = itemView.findViewById<TextView>(R.id.tvArtistName)
        private val trackTime = itemView.findViewById<TextView>(R.id.tvTrackTime)

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
                .into(songCover)
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        }
    }

}