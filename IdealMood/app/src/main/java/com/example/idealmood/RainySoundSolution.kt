package com.example.idealmood

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.idealmood.MyMusicService.LocalBinder
import kotlinx.android.synthetic.main.activity_rainy_sound_solution.*

class RainySoundSolution : AppCompatActivity() {
    var isPlay :Boolean = true
    private var mService :MyMusicService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rainy_sound_solution)
        init()
    }

    /*override fun onResume() {
        super.onResume()
        if(mService != null && isPlaying!!)
            firstPlay = false
        Log.i("서비스 : ", "$mService / $isPlaying")
    }*/

    /*private val mConnection :ServiceConnection = object :ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyMusicService.LocalBinder
            mService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
        }
    }*/

    // 서비스 실행 시 서비스에 있는 함수 호출
    //var isPlaying = mService?.getIsPlay()

    private fun init() {
        rain_playBtn.setOnClickListener {
            val intent = Intent(this, MyMusicService::class.java)
            intent.putExtra("playIdx", 0)
            //bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            startService(intent)
        }
    }
}