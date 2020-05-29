package com.example.idealmood

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// Activity에서 tab으로 화면을 전환하기 위한 adapter
class MyFragStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        //TODO("Not yet implemented")
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        //TODO("Not yet implemented")
        return when (position){
            0 -> SolutionFragment()
            1 -> MainFragment()
            2 -> SolutionFragment() // 감쓰 화면으로 수정
            else -> MainFragment()
        }
    }

}