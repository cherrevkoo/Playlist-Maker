package com.practicum.playlist_maker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
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

        findViewById<MaterialTextView>(R.id.share_app).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_course))
            startActivity(Intent.createChooser(shareIntent, "Поделиться через"))
        }

        findViewById<MaterialTextView>(R.id.support).setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO,
                ("mailto:" + getString(R.string.student_email)).toUri())
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            startActivity(supportIntent)
        }

        findViewById<MaterialTextView>(R.id.agreement).setOnClickListener {
            val agreementIntent = Intent(
                Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement))
            )
            startActivity(agreementIntent)
        }

    }
}