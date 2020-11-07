package com.example.idealmood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_page)

        // 첫 실행되는 액티비티에 있어야 합니다.
        // 시작 엑티비티가 바뀌면 이 코드도 옮겨주세요
        GlobalContext.setContext(this)

        Handler().postDelayed({
            val intent: Intent = if (UserInfo.has(UserInfo.NAME_PASSED) && UserInfo.get(UserInfo.NAME_PASSED) == true) {
                Intent(this,  MainActivity::class.java)
            } else {
                Intent(this,  InitNameActivity::class.java)
            }

            startActivity(intent)
        }, 2000)

    }
}
