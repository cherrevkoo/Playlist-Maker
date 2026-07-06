package com.practicum.playlist_maker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.sharing.domain.ExternalNavigator

class ExternalNavigatorImpl(
    private val context: Context
) : ExternalNavigator {

    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.android_course))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "Поделиться через")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun openSupport() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(context.getString(R.string.student_email))
            )
            putExtra(
                Intent.EXTRA_SUBJECT,
                context.getString(R.string.email_subject)
            )
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(R.string.email_body)
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(
            Intent.createChooser(intent, "Выберите почтовое приложение")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun openTerms() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(context.getString(R.string.user_agreement))
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }
}