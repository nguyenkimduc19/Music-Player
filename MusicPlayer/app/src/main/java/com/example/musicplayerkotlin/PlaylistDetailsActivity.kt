package com.example.musicplayerkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicplayerkotlin.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetailsActivity : AppCompatActivity() {

    lateinit var binding : ActivityPlaylistDetailsBinding

    companion object{
        var currentPlaylistPosition : Int = -1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentPlaylistPosition = intent.extras?.get("index") as Int

    }
}