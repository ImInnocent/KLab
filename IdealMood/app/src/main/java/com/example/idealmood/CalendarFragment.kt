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
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.item_day.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class CalendarFragment : Fragment() , CalendarItemEditFragment.OnStateSelectedListener{

    lateinit var adapter:CalendarAdapter

    var selectedGrid = 0    //선택하면 해당 그리드로 포커스.

    companion object {
        val EMOJI:Map<Int, Int> = mapOf(
            Pair(1, R.drawable.emoji_angry),
            Pair(2, R.drawable.emoji_cry),
            Pair(3, R.drawable.emoji_sad),
            Pair(4, R.drawable.emoji_good),
            Pair(5, R.drawable.emoji_happy)
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = CalendarAdapter(this)
        adapter.itemClickListener = object: CalendarAdapter.OnItemClickListener{

            override fun OnItemClick(
                holder: CalendarAdapter.CalViewHolder,
                view: View,
                position: Int
            ) {
                //Toast.makeText(activity, position.toString(), Toast.LENGTH_SHORT).show()
                CalendarItemEditFragment().show(childFragmentManager!!, "calendarFragFrag")
                selectedGrid = position
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

        // debug
        println(stat)

        EMOJI[stat]?.let { StatValue?.setImageResource(it) }
    }

}
