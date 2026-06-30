package com.practicum.playlist_maker.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.practicum.playlist_maker.Creator
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.domain.api.SearchHistoryInteractor
import com.practicum.playlist_maker.domain.api.TracksInteractor
import com.practicum.playlist_maker.domain.models.Track
import com.practicum.playlist_maker.ui.PlayerActivity

class SearchActivity : AppCompatActivity() {
    private var searchQuery: String = ""
    private var lastQuery: String = ""
    private lateinit var searchEditText: EditText
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var retryButton: MaterialButton
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private lateinit var historyTitle: TextView
    private lateinit var historyClearButton: MaterialButton
    private lateinit var historyRecyclerView: RecyclerView
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private lateinit var progressBar: ProgressBar
    private var isClickAllowed = true
    private lateinit var tracksInteractor: TracksInteractor

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_QUERY", searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString("SEARCH_QUERY", "")
        searchEditText.setText(searchQuery)
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

        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryInteractor =
            Creator.provideSearchHistoryInteractor(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.search_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        searchEditText = findViewById(R.id.searchEditText)
        progressBar = findViewById(R.id.progressBar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tracksAdapter = TrackAdapter(mutableListOf())
        recyclerView.adapter = tracksAdapter
        recyclerView.visibility = View.GONE


        searchEditText.post {
            searchEditText.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        val clearButton = findViewById<ImageButton>(R.id.clearButton)

        searchEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            searchQuery = text.toString()

            searchRunnable?.let {
                searchHandler.removeCallbacks(it)
            }

            if (searchQuery.isNotBlank()) {
                searchRunnable = Runnable {
                    performSearch(searchQuery)
                }
                searchHandler.postDelayed(searchRunnable!!, 2000)
            }
        }

        clearButton.setOnClickListener {
            searchRunnable?.let {
                searchHandler.removeCallbacks(it)

            }
            searchEditText.text.clear()
            recyclerView.visibility = View.GONE
            placeholderLayout.visibility = View.GONE
            tracksAdapter.updateTracks(mutableListOf())

            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        placeholderLayout = findViewById(R.id.placeholderLayout)
        placeholderLayout.visibility = View.GONE

        retryButton = findViewById(R.id.placeholderRetryButton)
        retryButton.visibility = View.GONE

        retryButton.setOnClickListener {
            if (lastQuery.isNotEmpty()) {
                performSearch(lastQuery)
            }
        }

        val historyTracks = searchHistoryInteractor.getHistory()

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(historyTracks.toMutableList())
        historyRecyclerView.adapter = historyAdapter

        historyAdapter.updateTracks(historyTracks)

        searchEditText.doOnTextChanged { text, _, _, _ ->
            updateViewsVisibility()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
          updateViewsVisibility()
        }

        tracksAdapter.setOnItemClickListener { track ->
            if (clickDebounce()) {
                searchHistoryInteractor.addTrack(track)
                val updatedHistory = searchHistoryInteractor.getHistory()
                historyAdapter.updateTracks(updatedHistory)
                updateViewsVisibility()

                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("TRACK", track)
                Log.d("TrackIntent", "album=${track.collectionName}, year=${track.releaseDate}, genre=${track.primaryGenreName}, country=${track.country}")
                startActivity(intent)
            }
        }

        historyAdapter.setOnItemClickListener { track ->
            if (clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("TRACK", track)
                startActivity(intent)
            }
        }

        historyTitle = findViewById(R.id.historyTitle)

        historyClearButton = findViewById(R.id.clearHistoryButton)

        historyClearButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            historyAdapter.updateTracks(emptyList())
            updateViewsVisibility()
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        progressBar.visibility = View.VISIBLE

        lastQuery = query
        placeholderLayout.visibility = View.GONE
        recyclerView.visibility = View.GONE

        tracksInteractor.searchTracks(
            query,
            object : TracksInteractor.TrackConsumer {

                override fun consume(foundTracks: List<Track>) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        if (foundTracks.isEmpty()) {
                            showPlaceholder(false)
                        } else {
                            tracksAdapter.updateTracks(foundTracks.toMutableList())
                            recyclerView.visibility = View.VISIBLE
                            placeholderLayout.visibility = View.GONE
                        }
                    }
                }

                override fun onError() {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        showPlaceholder(true)
                    }
                }
            }
        )
    }
    private fun showPlaceholder(isError: Boolean) {
        placeholderLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        val placeholderImage = findViewById<ImageView>(R.id.placeholderImage)
        val placeholderTitle = findViewById<TextView>(R.id.placeholderTitle)
        val placeholderText = findViewById<TextView>(R.id.placeholderText)

        if (isError) {
            placeholderImage.setImageResource(R.drawable.placeholder_error)
            placeholderTitle.visibility = View.VISIBLE
            placeholderTitle.text = getString(R.string.internet_problem)
            placeholderText.text = getString(R.string.check_internet_connection)
            retryButton.visibility = View.VISIBLE
        } else {
            placeholderImage.setImageResource(R.drawable.placeholder_empty)
            placeholderTitle.visibility = View.GONE
            placeholderText.text = getString(R.string.nothing_is_found)
            retryButton.visibility = View.GONE
        }
    }

    private fun updateViewsVisibility() {
        val isSearchEmpty = searchEditText.text.isEmpty()
        val hasFocus = searchEditText.hasFocus()
        val hasHistory = historyAdapter.itemCount > 0

        if (isSearchEmpty && hasFocus && hasHistory) {
            historyRecyclerView.visibility = View.VISIBLE
            historyTitle.visibility = View.VISIBLE
            historyClearButton.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholderLayout.visibility = View.GONE

        } else if (!isSearchEmpty) {
            recyclerView.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.GONE
            historyTitle.visibility = View.GONE
            historyClearButton.visibility = View.GONE
            placeholderLayout.visibility = View.GONE

        } else {
            recyclerView.visibility = View.GONE
            historyRecyclerView.visibility = View.GONE
            historyTitle.visibility = View.GONE
            historyClearButton.visibility = View.GONE
            placeholderLayout.visibility = View.GONE

        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            searchHandler.postDelayed({ isClickAllowed = true }, 1000)
        }
        return current
    }
    override fun onDestroy() {
        super.onDestroy()

        searchRunnable?.let {
            searchHandler.removeCallbacks(it)
        }
    }
}