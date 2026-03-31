package com.practicum.playlist_maker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.switchmaterial.SwitchMaterial

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
            val addresses = arrayOf(getString(R.string.student_email))
            val subject = getString(R.string.email_subject)
            val body = getString(R.string.email_body)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, addresses)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(intent, "Выберите почтовое приложение"))
            } else {
                Toast.makeText(this, "Нет почтового приложения на устройстве", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<MaterialTextView>(R.id.agreement).setOnClickListener {
            val agreementIntent = Intent(
                Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement))
            )
            startActivity(agreementIntent)
        }

        val darkThemeSwitch = findViewById<SwitchMaterial>(R.id.dark_theme_switch)

        darkThemeSwitch.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

    }
}