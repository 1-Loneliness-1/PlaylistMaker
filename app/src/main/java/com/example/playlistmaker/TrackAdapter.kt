package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.utils.DimenConvertor

class TrackAdapter(
    private val tracks: List<Track>
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TrackViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        )

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songCover = itemView.findViewById<ImageView>(R.id.ivTrackCover)
        private val trackName = itemView.findViewById<TextView>(R.id.tvTrackName)
        private val artistName = itemView.findViewById<TextView>(R.id.tvArtistName)
        private val trackTime = itemView.findViewById<TextView>(R.id.tvTrackTime)

        fun bind(model: Track) {
            Glide.with(itemView)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.song_cover_placeholder)
                .centerCrop()
                .transform(RoundedCorners(DimenConvertor.dpToPx(2f, itemView.context)))
                .into(songCover)
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = model.trackTime
        }
    }

}