package com.ayushsabharwal.ad_library

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.core.view.setPadding
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.concurrent.TimeUnit

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
                val rootLayout = FrameLayout(context)

                val playerView = PlayerView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    useController = false
                }

                val countdownText = TextView(context).apply {
                    setTextColor(Color.WHITE)
                    textSize = 18f
                    setPadding(16)
                    setBackgroundColor(Color.parseColor("#80000000"))
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.END or Gravity.TOP
                        setMargins(16, 16, 16, 16)
                    }
                }

                val closeButton = TextView(context).apply {
                    text = "Close"
                    setTextColor(Color.WHITE)
                    textSize = 18f
                    setPadding(16)
                    setBackgroundColor(Color.parseColor("#80000000"))
                    visibility = View.GONE
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.END or Gravity.TOP
                        setMargins(16, 16, 16, 16)
                    }
                    setOnClickListener {
                        dialog.dismiss()
                    }
                }

                val skipButton = TextView(context).apply {
                    text = "Skip"
                    setTextColor(Color.WHITE)
                    textSize = 18f
                    setPadding(16)
                    setBackgroundColor(Color.parseColor("#80000000"))
                    visibility = View.GONE
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        gravity = Gravity.START or Gravity.TOP
                        setMargins(16, 16, 16, 16)
                    }
                    setOnClickListener {
                        dialog.dismiss()
                    }
                }

                rootLayout.addView(playerView)
                rootLayout.addView(countdownText)
                rootLayout.addView(closeButton)
                rootLayout.addView(skipButton)

                dialog.setContentView(rootLayout)
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
                player.playWhenReady = true
                playerView.player = player

                dialog.show()

                playerView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl))
                    context.startActivity(intent)
                }

                val handler = Handler(Looper.getMainLooper())

                val updateCountdown = object : Runnable {
                    override fun run() {
                        val duration = player.duration
                        val position = player.currentPosition
                        val remaining = duration - position
                        if (duration > 0 && remaining > 0) {
                            val seconds = TimeUnit.MILLISECONDS.toSeconds(remaining)
                            countdownText.text = "Ad ends in $seconds s"
                            handler.postDelayed(this, 100)
                        } else {
                            skipButton.visibility = View.GONE
                            countdownText.visibility = View.GONE
                            closeButton.visibility = View.VISIBLE
                        }
                    }
                }

                val showSkip = Runnable {
                    skipButton.visibility = View.VISIBLE
                }

                player.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_READY) {
                            handler.post(updateCountdown)
                            handler.postDelayed(showSkip, 5000)
                        } else if (state == Player.STATE_ENDED) {
                            countdownText.visibility = View.GONE
                            closeButton.visibility = View.VISIBLE
                        }
                    }
                })

            } catch (e: Exception) {
                Toast.makeText(context, "An error has occurred can't load ad", Toast.LENGTH_LONG)
                    .show()
                e.printStackTrace()
            }
        }, { error ->
            error.printStackTrace()
        })

        queue.add(stringRequest)
    }
}