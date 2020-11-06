package com.example.idealmood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_rainy_sound_solution.*
import kotlinx.android.synthetic.main.activity_wave_sound_solution.*

class WaveSoundSolution : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wave_sound_solution)
        init()
    }

    private fun init() {
        wave_playBtn.setOnClickListener {
            val intent = Intent(this, MyMusicService::class.java)
            intent.putExtra("playIdx", 1)
            //bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }
}