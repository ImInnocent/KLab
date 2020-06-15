package com.example.idealmood

import android.content.Intent
import android.drm.DrmStore
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

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
}