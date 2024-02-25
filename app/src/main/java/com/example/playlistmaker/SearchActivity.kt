package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.utils.DimenConvertor

class SearchActivity : AppCompatActivity() {

    private var searchEditText: EditText? = null
    private var currentTextInEditText: String? = ""

    companion object {
        const val TEXT_IN_SEARCH_EDIT_TEXT = "TEXT_IN_SEARCH_EDIT_TEXT"
        private val trackList: List<Track> = listOf(
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backToPreviousScreenButton = findViewById<ImageView>(R.id.ivBackToPreviousScreen)
        searchEditText = findViewById(R.id.etSearch)
        val clearSearchEditTextButton = findViewById<ImageView>(R.id.ivClearEditText)
        val searchTracksRecycler = findViewById<RecyclerView>(R.id.rvTrackSearchList)

        backToPreviousScreenButton.setOnClickListener { finish() }

        clearSearchEditTextButton.setOnClickListener {
            searchEditText?.setText("")
            clearSearchEditTextButton.isVisible = false
            searchEditText?.clearFocus()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        searchEditText?.doOnTextChanged { text, start, before, count ->
            clearSearchEditTextButton.isVisible = !text.isNullOrEmpty()

            //Saving current text in edit text in variable for putting in Instance State
            currentTextInEditText = text.toString()
        }

        searchTracksRecycler.adapter = SearchAdapter(trackList)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(TEXT_IN_SEARCH_EDIT_TEXT, currentTextInEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        currentTextInEditText = savedInstanceState.getString(TEXT_IN_SEARCH_EDIT_TEXT)
        searchEditText?.setText(currentTextInEditText)
    }

    //Implementation of ViewHolder and Adapter classes for recyclerview with results of track search
    private class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            artistName.text = if (model.artistName.length > 20) model.artistName.take(20)
                .plus("...") else model.artistName
            trackTime.text = model.trackTime
        }
    }

    private class SearchAdapter(
        private val tracks: List<Track>
    ) : RecyclerView.Adapter<SearchViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        )

        override fun getItemCount() = tracks.size

        override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
            holder.bind(tracks[position])
        }

    }

}