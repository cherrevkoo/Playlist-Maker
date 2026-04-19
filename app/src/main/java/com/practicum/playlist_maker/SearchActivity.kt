package com.practicum.playlist_maker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
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
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private var searchQuery: String = ""
    private var lastQuery: String = ""
    private lateinit var searchEditText: EditText
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var tracksAdapter: TrackAdapter

    private lateinit var historyAdapter: TrackAdapter
    private lateinit var api: ItunesApi
    private lateinit var retryButton: MaterialButton
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyTitle: TextView
    private lateinit var historyClearButton: MaterialButton
    private lateinit var historyRecyclerView: RecyclerView

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

        val toolbar = findViewById<MaterialToolbar>(R.id.search_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        searchEditText = findViewById(R.id.searchEditText)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tracksAdapter = TrackAdapter(mutableListOf())
        recyclerView.adapter = tracksAdapter
        recyclerView.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ItunesApi::class.java)


        searchEditText.post {
            searchEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        val clearButton = findViewById<ImageButton>(R.id.clearButton)

        searchEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            searchQuery = text.toString()
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            recyclerView.visibility = View.GONE
            placeholderLayout.visibility = View.GONE
            tracksAdapter.updateTracks(mutableListOf())

            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        placeholderLayout = findViewById(R.id.placeholderLayout)
        placeholderLayout.visibility = View.GONE

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                }
                true
            } else false
        }

        retryButton = findViewById(R.id.placeholderRetryButton)
        retryButton.visibility = View.GONE

        retryButton.setOnClickListener {
            if (lastQuery.isNotEmpty()) {
                performSearch(lastQuery)
            }
        }

        val sharedPreferences = getSharedPreferences("playlist_maker_search_history", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        val historyTracks = searchHistory.getHistory()

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(searchHistory.getHistory().toMutableList())
        historyRecyclerView.adapter = historyAdapter

        historyAdapter.updateTracks(historyTracks)

        searchEditText.doOnTextChanged { text, _, _, _ ->
            updateViewsVisibility()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
          updateViewsVisibility()
        }

        tracksAdapter.setOnItemClickListener { track ->
            searchHistory.addTrack(track)
            val updatedHistory = searchHistory.getHistory()
            historyAdapter.updateTracks(updatedHistory)
            updateViewsVisibility()
        }

        historyTitle = findViewById(R.id.historyTitle)

        historyClearButton = findViewById(R.id.clearHistoryButton)

        historyClearButton.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.updateTracks(emptyList())
            updateViewsVisibility()
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        lastQuery = query
        placeholderLayout.visibility = View.GONE
        recyclerView.visibility = View.GONE

        val encodedQuery = URLEncoder.encode(query, "UTF-8")

        api.search(encodedQuery).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (!response.isSuccessful || response.body() == null) {
                    showPlaceholder(isError = true)
                    return
                }

                val body = response.body()!!
                if (body.resultCount == 0 || body.results.isNullOrEmpty()) {
                    showPlaceholder(isError = false)
                } else {
                    val tracksList = body.results.map { song ->
                        Track(
                            trackName = song.trackName ?: "",
                            artistName = song.artistName ?: "",
                            trackTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                                .format(song.trackTimeMillis ?: 0L),
                            artworkUrl100 = song.artworkUrl100 ?: "",
                            trackId = song.trackId
                        )
                    }.toMutableList()
                    tracksAdapter.updateTracks(tracksList)
                    recyclerView.visibility = View.VISIBLE
                    placeholderLayout.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showPlaceholder(isError = true)
            }
        })
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
}