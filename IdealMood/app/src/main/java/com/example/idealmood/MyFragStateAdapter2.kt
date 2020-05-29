package com.example.idealmood

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// 가운데 탭에서 화면을 전환하기 위한 adapter
class MyFragStateAdapter2(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        //TODO("Not yet implemented")
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        //TODO("Not yet implemented")
        return when (position){
            0 -> EmotionFragment()  // 현재 기분 상태
            1 -> CalendarFragment() // 감정 달력
            2 -> StatisticsFragment()   // 감정 통계
            else -> EmotionFragment()
        }
    }

}