package com.practicum.playlist_maker.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.practicum.playlist_maker.R
import com.practicum.playlist_maker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackIcon: ImageView = itemView.findViewById(R.id.track_icon)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)

    private val artistName: TextView = itemView.findViewById(R.id.artistName)

    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)



    fun bind(item: Track) {
        trackName.text = item.trackName
        artistName.text = item.artistName
        trackTime.text = SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(item.trackTime ?: 0L)

        val cornerRadiusInPx = (2 * itemView.context.resources.displayMetrics.density).toInt()


        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .centerCrop()
            .apply(RequestOptions().transform(RoundedCorners(cornerRadiusInPx)))
            .into(trackIcon)
    }
}