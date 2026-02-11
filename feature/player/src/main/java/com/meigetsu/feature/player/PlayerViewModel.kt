package com.meigetsu.feature.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    val player = ExoPlayer.Builder(context).build()

    fun playVideo(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
