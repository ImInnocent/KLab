package com.example.idealmood

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_emotion.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val textArray = arrayListOf<String>("솔루션", "내 감정", "감정 쓰레기통")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    override fun onStart() {
        super.onStart()
        // slide animation 추가
        //this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)

        var thread = Thread(Runnable {
            while (true) {
                runOnUiThread {
                    if (t32 != null) {
                        if (DataManager.getInstance().isStarted) {
                            t32.text =
                                "${75 + (Random().nextInt(10))}" //"${DataManager.getInstance().heartBeat} bpm"
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

    private fun init() {
        // 툴바 달고 아이콘 추가하기
        //setSupportActionBar(toolbar)
        // 툴바 아닌 앱 바 상태여도 아이콘 달림. 주석처리ㄴㄴ
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_favorite_white_28dp)

        // 탭 달기
        contents.adapter = MyFragStateAdapter(this)
        TabLayoutMediator(tabLayout, contents){ tab, position ->
            tab.text = textArray[position]
        }.attach()
        contents.isUserInputEnabled = false // 스트롤해서 탭 넘기는 기능 삭제
        contents.currentItem = 1




        // 심박수 업데이트
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

    public fun changeFragment(f: Fragment, cleanStack:Boolean = false){
        //프래그먼트  화면 전환 -> 감정 쓰레기통에서 쓰일 예정


    }

}