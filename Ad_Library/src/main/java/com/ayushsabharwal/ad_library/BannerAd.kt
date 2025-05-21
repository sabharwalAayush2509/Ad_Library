package com.ayushsabharwal.ad_library

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ayushsabharwal.ad_library.databinding.AdBannerBinding
import org.json.JSONObject

class BannerAd @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding = AdBannerBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        loadBannerAd()
    }

    private fun loadBannerAd() {
        val queue = Volley.newRequestQueue(context)
        val url = "https://dummyjson.com/c/6ec9-05a4-494e-8232"

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            try {
                binding.progressBar.visibility = View.GONE
                binding.bannerAd.visibility = View.VISIBLE
                val jsonObject = JSONObject(response)
                val imageUrl = jsonObject.getString("image_url")
                val clickUrl = jsonObject.getString("click_url")

                Glide.with(context).load(imageUrl).into(binding.bannerAd)

                binding.root.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl))
                    context.startActivity(intent)
                }

            } catch (e: Exception) {
                Toast.makeText(context, "An error has occurred can't load add", Toast.LENGTH_LONG)
                    .show()
                e.printStackTrace()
            }
        }, { error -> error.printStackTrace() })

        queue.add(stringRequest)
    }
}