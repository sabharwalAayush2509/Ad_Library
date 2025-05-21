package com.ayushsabharwal.ad_library

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.KeyEvent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object InterstitialAd {

    fun loadAndShow(context: Context) {
        val queue = Volley.newRequestQueue(context)
        val url = "https://dummyjson.com/c/4596-ef85-4882-b397"

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            try {
                val jsonObject = JSONObject(response)
                val videoUrl = jsonObject.getString("video_url")
                val clickUrl = jsonObject.getString("click_url")

                val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                val playerView = PlayerView(context)

                playerView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                playerView.useController = false
                dialog.setContentView(playerView)

                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setOnKeyListener { _, keyCode, _ ->
                    keyCode == KeyEvent.KEYCODE_BACK
                }

                dialog.window?.decorView?.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                val player = ExoPlayer.Builder(context).build()
                val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
                player.setMediaItem(mediaItem)
                player.prepare()
                playerView.player = player
                player.playWhenReady = true

                dialog.show()

                playerView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl))
                    context.startActivity(intent)
                }

                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            dialog.dismiss()
                            player.release()
                        }
                    }
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })

        queue.add(stringRequest)
    }
}