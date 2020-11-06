package com.example.idealmood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_firewall_sound_solution.*
import kotlinx.android.synthetic.main.activity_rainy_sound_solution.*

class FirewallSoundSolution : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firewall_sound_solution)
        init()
    }

    private fun init() {
        fire_playBtn.setOnClickListener {
            val intent = Intent(this, MyMusicService::class.java)
            intent.putExtra("playIdx", 2)
            //bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }
}