package com.example.idealmood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*

// 조혜진 이 구현한 setting 액티비티인데 일단은 안쓰고 냅두게 되었다.
// 하지만 애니메이션 효과 등은 충분히 쓸모가 있으므로 나중에 화면전환하는 일이 있으면 써먹기로 하자.^^....

class SettingsActivity : AppCompatActivity() {

    var onStartCount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            this.overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left)
        }

        init()
    }

    private fun init() {
        // 툴바 달고 아이콘 추가하기
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_28dp)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item!!.itemId) {
            android.R.id.home -> {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
