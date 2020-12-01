package com.example.idealmood

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.mikephil.charting.data.Entry
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_emotion.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    val textArray = arrayListOf<Int>(
            R.string.bottom_navi_solution,
            R.string.bottom_navi_emotion,
            R.string.bottom_navi_emo_trash)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    override fun onStart() {
        super.onStart()
        // slide animation 추가
        //this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)

        // 네비게이션 세팅
        navView.getHeaderView(0).findViewById<TextView>(R.id.navHeaderName).text = UserInfo.name ?: "김건국"

        var thread = Thread(Runnable {
            while (true) {
                runOnUiThread {
                    // rage point
                    if (t12 != null) {
                        if (DataManager.getInstance().isStarted) {
                            var rage:Int = DataManager.getInstance().lastRage
                            t12.text = getString(R.string.emotion_title_rage_number, rage)
                        } else {
                            t12.text = "----"
                        }
                    }

                    // rage time
                    if (t22 != null) {
                        if (DataManager.getInstance().isStarted) {
                            // seconds to minute
//                            var rageTime:Int = DataManager.getInstance().todayRageTime / 60
                            var rageTime:Int = DataManager.getInstance().todayRageTime
                            t22.text = getString(R.string.emotion_title_rage_time_number, (rageTime / 60).toInt())
                            if(DataManager.getInstance().isHigh)
                                createNotification()

                        } else {
                            t22.text = "----"
                        }
                    }

                    // heartbeat
                    if (t32 != null) {
                        if (DataManager.getInstance().isStarted) {
                            var heartBeat:Int = DataManager.getInstance().lastHeartBeat
                            t32.text = getString(R.string.emotion_title_heart_bpm_number, heartBeat)
                        } else {
                            t32.text = "----"
                        }
                    }
                }

                Thread.sleep(1000)
            }
        })

        thread.start()
    }

    fun createNotification(){
        //알람 콘텐츠 설정
        /*val channelID = "channel_01"
        val channelName = "stresshigh"
        //알림 관리 관리자 객체소환
        val notificationManager: NotificationManager = ContextCompat.getSystemService(Context!!.NOTIFICATION_SERVICE) as NotificationManager
        //builder객체
        val builder: NotificationCompat.Builder
        //알림 객체 만들기
        val channel: NotificationChannel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)

        //알림 매니저에게 채널 객체 생성요청
        notificationManager.createNotificationChannel(channel)
        //builder 객체 생성
        builder = NotificationCompat.Builder(Context!!, channelID)
        //builder.setSmallIcon(android.R.drawable.)
        builder.setContentTitle("Stress Level high")
        builder.setContentText("Run app and access solution contents!")
        val bm: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.emotion2_face2)
        builder.setLargeIcon(bm)

        val notification: Notification = builder.build()
        notificationManager.notify(1, notification)

        //notification.cancel(1)
*/
    }

    private fun init() {
        // 툴바 달고 아이콘 추가하기
        //setSupportActionBar(toolbar)
        // 툴바 아닌 앱 바 상태여도 아이콘 달림. 주석처리ㄴㄴ
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_favorite_white_28dp)

        // 탭 달기
        contents.adapter = MyFragStateAdapter(this)
        TabLayoutMediator(tabLayout, contents){ tab, position ->
            tab.text = getString(textArray[position])
        }.attach()
        contents.isUserInputEnabled = false // 스트롤해서 탭 넘기는 기능 삭제
        contents.currentItem = 1


        //navigationview 아이템 클릭 이벤트
        (navView as NavigationView).setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item2-> {
                    DrawerEmergencySetting().show(supportFragmentManager, "emergencyCall")
                    drawerLayout.closeDrawer(GravityCompat.END)
                    return@setNavigationItemSelectedListener true
                }
                R.id.item3-> {
                    DrawerThemeSetting().show(supportFragmentManager, "appthemecolor")
                    drawerLayout.closeDrawer(GravityCompat.END)
                    return@setNavigationItemSelectedListener true
                }
                R.id.item4-> {
                    DrawerSetting().show(supportFragmentManager, "settings")
                    drawerLayout.closeDrawer(GravityCompat.END)
                    return@setNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_actions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId) {
            R.id.action_drawer -> {
                //val i = Intent(this, SettingsActivity::class.java)
                //startActivity(i)
                //drawer 펼치는 action
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }
        return super.onOptionsItemSelected(item)
    }



    public fun setThemeColor(colorNum:Int){
        when(colorNum){
            0->{//colorAccent
                supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)))

            }
            1->{//CCoral
                supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.CCoral)))

            }
            2->{//CLightSkyblue
                supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.CLightSkyBlue)))

            }
            3->{//CGray
                supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.CGray)))

            }
        }
    }


}