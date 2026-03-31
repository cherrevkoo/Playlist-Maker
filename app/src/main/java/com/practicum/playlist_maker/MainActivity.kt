package com.practicum.playlist_maker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<MaterialButton>(R.id.search_button).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.media_button).setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.settings_button).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val searchBtn = findViewById<MaterialButton>(R.id.search_button)
        searchBtn.typeface = ResourcesCompat.getFont(this, R.font.ys_display_medium)
    }
}