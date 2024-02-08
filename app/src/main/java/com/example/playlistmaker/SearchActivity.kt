package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backToPreviousScreenButton = findViewById<ImageView>(R.id.back_button)
        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearSearchEditTextButton = findViewById<ImageView>(R.id.clear_edit_text_button)

        backToPreviousScreenButton.setOnClickListener { finish() }

        clearSearchEditTextButton.setOnClickListener {
            searchEditText.setText("")
            clearSearchEditTextButton.visibility = View.GONE
            searchEditText.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val searchEditTextListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    clearSearchEditTextButton.visibility = View.GONE
                } else {
                    clearSearchEditTextButton.visibility = View.VISIBLE
                }
            }
        }

        searchEditText.addTextChangedListener(searchEditTextListener)

    }
}