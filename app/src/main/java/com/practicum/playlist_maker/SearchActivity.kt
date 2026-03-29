package com.practicum.playlist_maker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var searchQuery: String = ""

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_QUERY", searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString("SEARCH_QUERY", "")
        findViewById<EditText>(R.id.searchEditText).setText(searchQuery)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.search_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val clearIcon = getDrawable(R.drawable.ic_clear)



        val searchEditText = findViewById<EditText>(R.id.searchEditText)

        searchEditText.post {
            searchEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        searchEditText.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP &&
                searchEditText.compoundDrawablesRelative[2] != null &&
                event.x >= searchEditText.width - searchEditText.paddingEnd - searchEditText.compoundDrawablesRelative[2].bounds.width()
                ) {
                searchEditText.text.clear()

                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)

                true
            } else false
        }

        val clearTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    searchEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        searchEditText.compoundDrawablesRelative[0],
                        null,
                        null,
                        null
                    )
                } else {
                    searchEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(searchEditText.compoundDrawablesRelative[0],
                        null, clearIcon,
                        null )
                }

                searchQuery = s.toString()
            }
        }

        searchEditText.addTextChangedListener(clearTextWatcher)
    }
}