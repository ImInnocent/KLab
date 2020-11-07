package com.example.idealmood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_init_name.*
import org.jetbrains.anko.toast

class InitNameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init_name)

        initNameSkipBtn.setOnClickListener {
            UserInfo.set(UserInfo.NAME, UserInfo.DEFAULT_NAME)
            UserInfo.set(UserInfo.NAME_PASSED, true)

            nextActivity()
        }

        initNameConfirmBtn.setOnClickListener {
            if (initNameText.text.isEmpty()) {
                toast("입력해주세요")
                return@setOnClickListener
            }

            UserInfo.set(UserInfo.NAME, initNameText.text)
            UserInfo.set(UserInfo.NAME_PASSED, true)

            nextActivity()
        }
    }

    private fun nextActivity() {
        val intent = Intent(this,  MainActivity::class.java)
        startActivity(intent)
    }
}