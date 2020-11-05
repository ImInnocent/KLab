package com.example.idealmood

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MyMusicService : Service() {
    lateinit var myPlayer: MediaPlayer

    public class LocalBinder : Binder() {
        public fun getService() : MyMusicService {
            return MyMusicService()
        }
    }
    private var mBinder = LocalBinder()

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    //private var mConnection =

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var isPlay: Boolean? = intent?.extras?.getBoolean("isPlay")
        if(isPlay!!) {
            myPlayer = MediaPlayer.create(this, R.raw.rainysound)
            myPlayer.start()
        }
        else {
            myPlayer.stop()
            myPlayer.release()
        }

        //return super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }
}