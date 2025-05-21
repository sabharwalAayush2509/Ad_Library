package com.ayushsabharwal.adlibrary

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ayushsabharwal.adlibrary.databinding.ActivityScreenMainBinding

class MainScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScreenMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activityScreenMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bannerAd.setOnClickListener {
            val intent = Intent(this, BannerAdActivity::class.java)
            startActivity(intent)
        }

        binding.interstitialAd.setOnClickListener {
            val intent = Intent(this, InterstitialAdActivity::class.java)
            startActivity(intent)
        }
    }
}