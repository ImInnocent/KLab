package com.example.idealmood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import kotlinx.android.synthetic.main.activity_deep_breath_solution.*

class DeepBreathSolution : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deep_breath_solution)
        init()
    }

    private fun init() {
        val gifImage = GlideDrawableImageViewTarget(gifImage)
        Glide.with(this).load(R.drawable.deepbreathsol).into(gifImage)
        backBtn.setOnClickListener {
            onBackPressed() // 뒤로 가기 효과
        }
    }
}
