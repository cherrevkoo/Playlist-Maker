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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
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

class PlayerActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var trackCover: ImageView
    private lateinit var playButton: ImageButton
    private lateinit var likeButton: ImageButton
    private var isPlaying: Boolean = false
    private var isLiked: Boolean = false
    private var trackName: String = ""
    private var artistName: String = ""
    private var albumName: String = ""
    private var releaseDate: String = ""
    private var genre: String = ""
    private var country: String = ""
    private var trackTime: String = ""
    private var artworkUrl: String = ""

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

        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            isPlaying = !isPlaying
            val drawableRes = if (isPlaying) R.drawable.track_is_playing else R.drawable.button_play
            playButton.setImageResource(drawableRes)
        }

        likeButton.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                likeButton.setImageResource(R.drawable.button_already_liked)
            } else {
                likeButton.setImageResource(R.drawable.button_like)
            }
        }


        intent?.let {
            trackName = it.getStringExtra("TRACK_NAME") ?: ""
            artistName = it.getStringExtra("ARTIST_NAME") ?: ""
            albumName = it.getStringExtra("ALBUM_NAME") ?: ""
            releaseDate = it.getStringExtra("RELEASE_DATE") ?: ""
            genre = it.getStringExtra("GENRE") ?: ""
            country = it.getStringExtra("COUNTRY") ?: ""
            trackTime = it.getStringExtra("TRACK_TIME") ?: ""
            artworkUrl = it.getStringExtra("ARTWORK_URL") ?: ""
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
        }

        updateUI()
        saveCurrentTrack()
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
}