package com.project.musicplayer2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.musicplayer2.databinding.ActivityPlaylistBinding

class PlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayer2)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}