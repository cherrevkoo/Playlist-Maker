package com.practicum.playlist_maker.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.creator.Creator
import com.practicum.playlist_maker.search.ui.TrackParcelable

class PlayerActivity : AppCompatActivity() {

    private val viewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(Creator.providePlayerInteractor(this))
    }

    private lateinit var backButton: ImageButton
    private lateinit var trackCover: ImageView
    private lateinit var playButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var playTime: TextView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var albumName: TextView
    private lateinit var releaseYear: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var duration: TextView
    private var lastArtworkUrl: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backButton = findViewById(R.id.backButton)
        trackCover = findViewById(R.id.track_cover)
        playButton = findViewById(R.id.play_button)
        likeButton = findViewById(R.id.button_like)
        playTime = findViewById(R.id.play_time)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        albumName = findViewById(R.id.value_album)
        releaseYear = findViewById(R.id.value_year)
        genre = findViewById(R.id.value_genre)
        country = findViewById(R.id.value_country)
        duration = findViewById(R.id.value_duration)

        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            viewModel.onPlayPauseClick()
        }

        likeButton.setOnClickListener {
            viewModel.onLikeClick()
        }

        viewModel.observeState().observe(this) { state ->
            trackName.text = state.trackName
            artistName.text = state.artistName
            albumName.text = state.albumName
            releaseYear.text = state.releaseYear
            genre.text = state.genre
            country.text = state.country
            duration.text = state.duration
            playTime.text = state.playTime
            playButton.setImageResource(
                if (state.isPlaying) R.drawable.track_is_playing else R.drawable.button_play
            )
            likeButton.setImageResource(
                if (state.isLiked) R.drawable.button_already_liked else R.drawable.button_like
            )
            if (state.artworkUrl != lastArtworkUrl) {
                lastArtworkUrl = state.artworkUrl
                loadCover(state.artworkUrl)
            }
        }

        val track = intent.getParcelableExtra<TrackParcelable>("TRACK")
        viewModel.loadTrack(track)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onScreenPause()
    }

    private fun loadCover(artworkUrl: String) {
        val cornerRadiusInPx = (8 * resources.displayMetrics.density).toInt()
        if (artworkUrl.isNotEmpty()) {
            val highResUrl = artworkUrl.replace("100x100bb.jpg", "512x512bb.jpg")
            Glide.with(this)
                .load(highResUrl)
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions().transform(RoundedCorners(cornerRadiusInPx)))
                .into(trackCover)
        } else {
            trackCover.setImageResource(R.drawable.placeholder)
        }
    }
}
