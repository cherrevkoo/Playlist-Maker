package com.practicum.playlist_maker.search.ui

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.creator.Creator
import com.practicum.playlist_maker.player.ui.PlayerActivity

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    private lateinit var searchEditText: EditText
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var retryButton: MaterialButton
    private lateinit var historyTitle: TextView
    private lateinit var historyClearButton: MaterialButton
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var viewModel: SearchViewModel
    private var isClickAllowed = true
    private val clickHandler = Handler(Looper.getMainLooper())

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
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

        historyTitle = findViewById(R.id.historyTitle)
        historyClearButton = findViewById(R.id.clearHistoryButton)

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

        placeholderLayout = findViewById(R.id.placeholderLayout)
        placeholderLayout.visibility = View.GONE

        retryButton = findViewById(R.id.placeholderRetryButton)
        retryButton.visibility = View.GONE

        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(mutableListOf())
        historyRecyclerView.adapter = historyAdapter

        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(
                Creator.provideTracksInteractor(),
                Creator.provideSearchHistoryInteractor(this)
            )
        )[SearchViewModel::class.java]

        viewModel.observeState().observe(this) { state ->
            when (state) {
                is SearchState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    placeholderLayout.visibility = View.GONE
                    historyRecyclerView.visibility = View.GONE
                    historyTitle.visibility = View.GONE
                    historyClearButton.visibility = View.GONE
                }

                is SearchState.Content -> {
                    progressBar.visibility = View.GONE
                    tracksAdapter.updateTracks(state.tracks)
                    recyclerView.visibility = View.VISIBLE
                    placeholderLayout.visibility = View.GONE
                    historyRecyclerView.visibility = View.GONE
                    historyTitle.visibility = View.GONE
                    historyClearButton.visibility = View.GONE
                }

                is SearchState.Empty -> {
                    progressBar.visibility = View.GONE
                    showPlaceholder(false)
                    historyRecyclerView.visibility = View.GONE
                    historyTitle.visibility = View.GONE
                    historyClearButton.visibility = View.GONE
                }

                is SearchState.Error -> {
                    progressBar.visibility = View.GONE
                    showPlaceholder(true)
                    historyRecyclerView.visibility = View.GONE
                    historyTitle.visibility = View.GONE
                    historyClearButton.visibility = View.GONE
                }

                is SearchState.Idle -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    historyRecyclerView.visibility = View.GONE
                    historyTitle.visibility = View.GONE
                    historyClearButton.visibility = View.GONE
                    placeholderLayout.visibility = View.GONE
                }

                is SearchState.History -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    placeholderLayout.visibility = View.GONE
                    historyAdapter.updateTracks(state.tracks)
                    if (state.tracks.isEmpty()) {
                        historyRecyclerView.visibility = View.GONE
                        historyTitle.visibility = View.GONE
                        historyClearButton.visibility = View.GONE
                    } else {
                        historyRecyclerView.visibility = View.VISIBLE
                        historyTitle.visibility = View.VISIBLE
                        historyClearButton.visibility = View.VISIBLE
                    }
                }
            }
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            viewModel.onSearchTextChanged("", searchEditText.hasFocus())

            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        retryButton.setOnClickListener {
            viewModel.retrySearch()
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            viewModel.onSearchTextChanged(text?.toString().orEmpty(), searchEditText.hasFocus())
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusChanged(hasFocus)
        }

        tracksAdapter.setOnItemClickListener { track ->
            if (clickDebounce()) {
                viewModel.addTrack(track)

                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("TRACK", track.toParcelable())
                Log.d("TrackIntent", "album=${track.collectionName}, year=${track.releaseDate}, genre=${track.primaryGenreName}, country=${track.country}")
                startActivity(intent)
            }
        }

        historyAdapter.setOnItemClickListener { track ->
            if (clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("TRACK", track.toParcelable())
                startActivity(intent)
            }
        }

        historyClearButton.setOnClickListener {
            viewModel.clearHistory()
        }
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
    fun clickDebounce(): Boolean {

        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickHandler.postDelayed({
                isClickAllowed = true
            }, CLICK_DEBOUNCE_DELAY)
        }
        return current

    }
}