package com.example.idealmood

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.io.File
import kotlin.properties.Delegates

class MyMusicService : Service() {
    var myPlayer :MediaPlayer? = null
    val screenList = arrayOf<AppCompatActivity>(RainySoundSolution(), WaveSoundSolution())
    //var mActivityMessenger :Messenger ?= null

    /*companion object {
        const val PLAYING = 0
        const val STOPING = 1
    }

    class ServiceHandler : Handler() {
        override fun handleMessage(msg: Message) {
            if(msg.what == PLAYING) {
                myPlayer.stop()
                myPlayer.release()
            }
        }
    }*/

    class LocalBinder : Binder() {
            fun getService(): MyMusicService {
                return MyMusicService() // 현재 서비스 반환
            }
    }

    companion object {
        private val mBinder :IBinder = LocalBinder() // 객체 만들기
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder  // onBind함수의 return값으로 사용
    }

    /*fun getIsPlay() :Boolean{
        return myPlayer.isPlaying
    }*/


    /*private var mConnection :ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            //TODO("Not yet implemented")
        }
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            //TODO("Not yet implemented")
        }
    }*/

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var playIdx = intent?.extras?.getInt("playIdx")
        var music = when (playIdx){
            0 -> R.raw.rainysound
            1 -> R.raw.oceanwavesound
            else -> null
        }

        val mNotifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(myPlayer == null) {  // Play 버튼 눌렀을 때
            myPlayer = MediaPlayer.create(this, music!!)
            myPlayer!!.start()

            val msgIntent = Intent(this, screenList[playIdx!!]::class.java)
            val msgPendingIntent = PendingIntent.getActivity(
                this, 1, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            // API 26버전부터 추가
            val mchannel = NotificationChannel("1", "rainySound", NotificationManager.IMPORTANCE_DEFAULT)
            mNotifyMgr.createNotificationChannel(mchannel)

            val mBuilder =  NotificationCompat.Builder(this, "1")
            with(mBuilder) {
                setSmallIcon(R.drawable.ic_favorite_white_28dp)
                setContentTitle("솔루션 재생 중")
                setContentIntent(msgPendingIntent)
                setContentText("탭하여 앱 실행하기")
            }
            mNotifyMgr.notify(1, mBuilder.build())  // id는 나중에 알림 제거할 때 사용
        }
        else {  // stop 버튼 눌렀을 때
            myPlayer!!.stop()
            myPlayer!!.release()
            stopService(intent)
            mNotifyMgr.cancel(1)
        }

        //return super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }
}