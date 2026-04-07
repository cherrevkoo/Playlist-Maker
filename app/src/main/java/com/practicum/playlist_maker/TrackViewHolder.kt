package com.practicum.playlist_maker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackIcon: ImageView = itemView.findViewById(R.id.track_icon)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistAndTime: TextView = itemView.findViewById(R.id.artist_and_time)

    fun bind(item: Track) {
        trackName.text = item.trackName
        artistAndTime.text = itemView.context.getString(R.string.artist_and_time, item.artistName, item.trackTime)
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .apply(RequestOptions().transform(RoundedCorners(2)))
            .into(trackIcon)
    }
}