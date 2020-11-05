package com.example.idealmood

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_rainy_sound_solution.*

class RainySoundSolution : AppCompatActivity() {
    var isPlay :Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rainy_sound_solution)
        init()
    }

    private fun init() {
        playBtn.setOnClickListener {
            val intent = Intent(this, MyMusicService::class.java)
            if (isPlay == false) {  // "PLAY" 버튼을 눌렀을 때
                isPlay = true
                playBtn.text = "STOP"
                intent.putExtra("isPlay", true)
            }
            else {  // "STOP" 버튼을 눌렀을 때
                isPlay = false
                playBtn.text = "PLAY"
                intent.putExtra("isPlay", false)
            }
        }
    }
}