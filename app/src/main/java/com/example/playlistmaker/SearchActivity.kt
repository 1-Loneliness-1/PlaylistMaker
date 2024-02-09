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

    private lateinit var searchEditText: EditText
    private var currentTextInEditText: String? = ""

    companion object {
        const val TEXT_IN_SEARCH_EDIT_TEXT = "TEXT_IN_SEARCH_EDIT_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backToPreviousScreenButton = findViewById<ImageView>(R.id.back_button)
        searchEditText = findViewById(R.id.search_edit_text)
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

                //Saving current text in edit text in variable for putting in Instance State
                currentTextInEditText = s.toString()
            }
        }

        searchEditText.addTextChangedListener(searchEditTextListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(TEXT_IN_SEARCH_EDIT_TEXT, currentTextInEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        currentTextInEditText = savedInstanceState.getString(TEXT_IN_SEARCH_EDIT_TEXT)
        searchEditText.setText(currentTextInEditText)
    }
}