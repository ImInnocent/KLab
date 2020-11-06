package com.example.idealmood

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_meditation_solution.*

class MeditationSolution : AppCompatActivity() {

    lateinit var mediPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_solution)
        init()
    }

    private fun init() {
        medi_backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        // 해당 액티비티가 실행되면 음악이 자동으로 재생
        mediPlayer = MediaPlayer.create(this, R.raw.meditationsound)
        mediPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        mediPlayer.stop()
        mediPlayer.release()
    }
}
