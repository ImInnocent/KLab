package com.example.idealmood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class CalendarFragment : Fragment() {

    lateinit var adapter:CalendarAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = CalendarAdapter(this)

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
        val sdf = SimpleDateFormat("yyyy MM", Locale.KOREAN)
        currMonth.text = sdf.format(calendar.time)
    }

}
