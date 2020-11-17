package com.example.idealmood

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.util.Util
import com.google.android.material.internal.NavigationMenu
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.item_day.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class CalendarFragment : Fragment() , CalendarItemEditFragment.OnStateSelectedListener{

    lateinit var adapter:CalendarAdapter
    lateinit var myDBHelper :MyDBHelper

    var selectedGrid = 0    //선택하면 해당 그리드로 포커스.
    var myDate = ""   // 선택한 grid의 날짜

    /*companion object {
        val EMOJI:Map<Int, Int> = mapOf(
            Pair(1, R.drawable.emoji_angry),
            Pair(2, R.drawable.emoji_cry),
            Pair(3, R.drawable.emoji_sad),
            Pair(4, R.drawable.emoji_good),
            Pair(5, R.drawable.emoji_happy)
        )
    }*/

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        calendarTitle.text = UserInfo.get(UserInfo.NAME).toString() + resources.getString(R.string.calender_title);

        myDBHelper = MyDBHelper(requireContext())
        adapter = CalendarAdapter(this, myDBHelper)
        adapter.itemClickListener = object: CalendarAdapter.OnItemClickListener{

            override fun OnItemClick(
                holder: CalendarAdapter.CalViewHolder,
                view: View,
                position: Int,
                baseCalendar: BaseCalendar
            ) {
                //Toast.makeText(activity, position.toString(), Toast.LENGTH_SHORT).show()
                CalendarItemEditFragment().show(childFragmentManager!!, "calendarFragFrag")
                selectedGrid = position
                myDate = currMonth.text.toString() + " " + baseCalendar.data[position].toString()
            }

        }

        rcyCalendarView.layoutManager = GridLayoutManager(activity, BaseCalendar.DAYS_OF_WEEK)
        rcyCalendarView.adapter = adapter
        rcyCalendarView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.HORIZONTAL))
        rcyCalendarView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        prevMonthBtn.setOnClickListener {
            adapter.changeToPrevMonth()
        }

        nextMonthBtn.setOnClickListener {
            adapter.changeToNextMonth()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    fun refreshCurrentMonth(calendar: Calendar) {
        val sdf = SimpleDateFormat("yyyy MM", UtilManager.currentLocale())
        currMonth.text = sdf.format(calendar.time)
    }

    override fun OnStateSelectedListener(stat: Int) {
        //stat값으로 얼굴 모양 바꾸게 하기.(NestedFragment로부터data전송받음)
        var nowView = rcyCalendarView.layoutManager?.findViewByPosition(selectedGrid);
        var StatValue = nowView?.findViewById<ImageView>(R.id.daystatus)

        println(stat)        // debug
        //EMOJI[stat]?.let { StatValue?.setImageResource(it) }

        // DB에 삽입
        if(myDBHelper.CD_findOneData(myDate).emotion == 0) {    // 해당 data가 없으면 insert하기
            myDBHelper.CD_insertData(MyCalendar(stat, -1, myDate))   // 스트레스 수치는 나중에 추가할 예정임!!!
        }
        else {  // 이미 존재할 경우 update하기
            myDBHelper.CD_updateData(stat, myDate)
        }
        adapter.notifyDataSetChanged()
    }

}
