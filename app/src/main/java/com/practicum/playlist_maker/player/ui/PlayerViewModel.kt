package com.practicum.playlist_maker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlist_maker.player.domain.api.PlayerInteractor
import com.practicum.playlist_maker.search.ui.TrackParcelable
import com.practicum.playlist_maker.search.ui.toParcelable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData(PlayerState())
    private val mediaPlayer = MediaPlayer()
    private var playerStatus = PlayerStatus.DEFAULT
    private var playWhenPrepared = false
    private var timerJob: Job? = null
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    fun observeState(): LiveData<PlayerState> = stateLiveData

    fun loadTrack(track: TrackParcelable?) {
        val loadedTrack = track ?: playerInteractor.getTrack()?.toParcelable() ?: return
        playerInteractor.saveTrack(loadedTrack.toDomain())
        renderState(createStateFromTrack(loadedTrack))
        preparePlayer(loadedTrack.previewUrl.orEmpty())
    }

    fun onPlayPauseClick() {
        when (playerStatus) {
            PlayerStatus.PLAYING -> pausePlayer()
            PlayerStatus.PREPARED, PlayerStatus.PAUSED -> startPlayer()
            PlayerStatus.DEFAULT -> playWhenPrepared = true
        }
    }

    fun onLikeClick() {
        updateState { copy(isLiked = !isLiked) }
    }

    fun onScreenPause() {
        if (playerStatus == PlayerStatus.PLAYING) {
            pausePlayer()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        mediaPlayer.release()
    }

    private fun preparePlayer(previewUrl: String) {
        if (previewUrl.isEmpty()) return

        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerStatus = PlayerStatus.PREPARED
                if (playWhenPrepared) {
                    playWhenPrepared = false
                    startPlayer()
                }
            }
            mediaPlayer.setOnCompletionListener {
                playerStatus = PlayerStatus.PREPARED
                stopTimer()
                updateState {
                    copy(
                        isPlaying = false,
                        playTime = formatTime(mediaPlayer.currentPosition)
                    )
                }
            }
        } catch (e: Exception) {
            playerStatus = PlayerStatus.DEFAULT
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStatus = PlayerStatus.PLAYING
        updateState { copy(isPlaying = true) }
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerStatus = PlayerStatus.PAUSED
        updateState { copy(isPlaying = false) }
        stopTimer()
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (isActive && playerStatus == PlayerStatus.PLAYING) {
                updateState { copy(playTime = formatTime(mediaPlayer.currentPosition)) }
                delay(TIMER_UPDATE_DELAY)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun createStateFromTrack(track: TrackParcelable): PlayerState {
        return PlayerState(
            trackName = track.trackName.orEmpty(),
            artistName = track.artistName.orEmpty(),
            albumName = track.collectionName.orEmpty(),
            releaseYear = track.releaseDate
                ?.takeIf { it.length >= 4 }
                ?.substring(0, 4)
                .orEmpty(),
            genre = track.primaryGenreName.orEmpty(),
            country = track.country.orEmpty(),
            duration = track.trackTime?.let { dateFormat.format(it) }.orEmpty(),
            artworkUrl = track.artworkUrl100.orEmpty()
        )
    }

    private fun formatTime(timeMillis: Int): String {
        return dateFormat.format(timeMillis)
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.value = state
    }

    private fun updateState(transform: PlayerState.() -> PlayerState) {
        val currentState = stateLiveData.value ?: PlayerState()
        stateLiveData.value = currentState.transform()
    }

    private enum class PlayerStatus {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }

    private companion object {
        private const val TIMER_UPDATE_DELAY = 300L
    }
}
