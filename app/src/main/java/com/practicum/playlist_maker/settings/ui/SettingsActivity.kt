package com.practicum.playlist_maker.settings.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.creator.App
import com.practicum.playlist_maker.creator.Creator

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(
            Creator.provideSharingInteractor(applicationContext),
            Creator.provideSettingsInteractor(applicationContext)
        )
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar = findViewById<MaterialToolbar>(R.id.settings_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.dark_theme_switch)

        viewModel.observeState().observe(this) { state ->
            if (themeSwitcher.isChecked != state.isDarkTheme) {
                themeSwitcher.isChecked = state.isDarkTheme
            }

            AppCompatDelegate.setDefaultNightMode(
                if (state.isDarkTheme) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeSwitch(checked)
        }

        findViewById<MaterialTextView>(R.id.share_app).setOnClickListener {
            viewModel.onShareClick()
        }

        findViewById<MaterialTextView>(R.id.support).setOnClickListener {
            viewModel.onSupportClick()
        }

        findViewById<MaterialTextView>(R.id.agreement).setOnClickListener {
            viewModel.onTermsClick()
        }
    }
}