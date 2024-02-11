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
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged

class SearchActivity : AppCompatActivity() {

    private var searchEditText: EditText? = null
    private var currentTextInEditText: String? = ""

    companion object {
        const val TEXT_IN_SEARCH_EDIT_TEXT = "TEXT_IN_SEARCH_EDIT_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backToPreviousScreenButton = findViewById<ImageView>(R.id.ivBackToPreviousScreen)
        searchEditText = findViewById(R.id.etSearch)
        val clearSearchEditTextButton = findViewById<ImageView>(R.id.ivClearEditText)

        backToPreviousScreenButton.setOnClickListener { finish() }

        clearSearchEditTextButton.setOnClickListener {
            searchEditText?.setText("")
            clearSearchEditTextButton.isVisible = false
            searchEditText?.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        searchEditText?.doOnTextChanged { text, start, before, count ->
            clearSearchEditTextButton.isVisible = !text.isNullOrEmpty()

            //Saving current text in edit text in variable for putting in Instance State
            currentTextInEditText = text.toString()
        }
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
}