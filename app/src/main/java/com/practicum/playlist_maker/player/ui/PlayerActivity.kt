package com.practicum.playlist_maker.player.ui

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.search.ui.TrackParcelable
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var trackCover: ImageView
    private lateinit var playButton: ImageButton
    private lateinit var likeButton: ImageButton

    private lateinit var playTime: TextView

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var isLiked: Boolean = false
    private var trackName: String = ""
    private var artistName: String = ""
    private var albumName: String = ""
    private var releaseDate: String = ""
    private var genre: String = ""
    private var country: String = ""
    private var trackTime: String = ""
    private var artworkUrl: String = ""
    private var previewUrl: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private var timer: Runnable? = null
    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

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

        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playbackControl()
        }

        likeButton.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                likeButton.setImageResource(R.drawable.button_already_liked)
            } else {
                likeButton.setImageResource(R.drawable.button_like)
            }
        }

        val track = intent.getParcelableExtra<TrackParcelable>("TRACK")

        track?.let {
            trackName = it.trackName.toString()
            artistName = it.artistName.toString()
            albumName = it.collectionName ?: ""
            releaseDate = it.releaseDate ?: ""
            genre = it.primaryGenreName.toString()
            country = it.country.toString()
            trackTime = it.trackTime?.let { time ->
                dateFormat.format(time)
            } ?: ""
            artworkUrl = it.artworkUrl100.toString()
            previewUrl = it.previewUrl.toString()
        } ?: run {

            val prefs = getSharedPreferences("player_state", MODE_PRIVATE)
            trackName = prefs.getString("TRACK_NAME", "") ?: ""
            artistName = prefs.getString("ARTIST_NAME", "") ?: ""
            albumName = prefs.getString("ALBUM_NAME", "") ?: ""
            releaseDate = prefs.getString("RELEASE_DATE", "") ?: ""
            genre = prefs.getString("GENRE", "") ?: ""
            country = prefs.getString("COUNTRY", "") ?: ""
            trackTime = prefs.getString("TRACK_TIME", "") ?: ""
            artworkUrl = prefs.getString("ARTWORK_URL", "") ?: ""
            previewUrl = prefs.getString("PREVIEW_URL", "") ?: ""
        }

        updateUI()
        saveCurrentTrack()
        preparePlayer()
        }

    private fun saveCurrentTrack() {
        val prefs = getSharedPreferences("player_state", MODE_PRIVATE)
        prefs.edit()
            .putString("TRACK_NAME", trackName)
            .putString("ARTIST_NAME", artistName)
            .putString("ALBUM_NAME", albumName)
            .putString("RELEASE_DATE", releaseDate)
            .putString("GENRE", genre)
            .putString("COUNTRY", country)
            .putString("TRACK_TIME", trackTime)
            .putString("ARTWORK_URL", artworkUrl)
            .putString("PREVIEW_URL", previewUrl)
            .apply()

    }

    private fun updateUI() {
        findViewById<TextView>(R.id.trackName).text = trackName
        findViewById<TextView>(R.id.artistName).text = artistName
        findViewById<TextView>(R.id.value_album).text = albumName
        findViewById<TextView>(R.id.value_year).text =
            if (!releaseDate.isNullOrEmpty() && releaseDate.length >= 4) releaseDate.substring(0, 4) else ""
        findViewById<TextView>(R.id.value_genre).text = genre
        findViewById<TextView>(R.id.value_country).text = country
        findViewById<TextView>(R.id.value_duration).text = trackTime

        val cornerRadiusInPx = (8 * resources.displayMetrics.density).toInt()

        val trackCover = findViewById<ImageView>(R.id.track_cover)

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

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.button_play)
            playerState = STATE_PREPARED
            playTime.text = dateFormat.format(mediaPlayer.currentPosition)
            timer?.let {
                handler.removeCallbacks(it)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.track_is_playing)
        playerState = STATE_PLAYING
        timer = createUpdateTimer()
        handler.post(timer!!)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.button_play)
        playerState = STATE_PAUSED
        timer?.let {
            handler.removeCallbacks(it)
        }
    }

    override fun onPause() {
        super.onPause()

        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.let {
            handler.removeCallbacks(it)
        }
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED,
            STATE_PAUSED -> startPlayer()
        }
    }
    private fun createUpdateTimer() : Runnable {
        return object : Runnable {
            override fun run(){
                if (playerState == STATE_PLAYING) {
                    playTime.text = dateFormat.format(mediaPlayer.currentPosition)
                    handler.postDelayed(this, TIMER_UPDATE_DELAY)
                }
            }
        }
    }
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val TIMER_UPDATE_DELAY = 300L
    }
}