package com.example.idealmood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class CalendarFragment : Fragment() {

    lateinit var adapter:CalendarAdapter


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        /*childFragmentManager.setResultListener("requestKey") { key, bundle ->
            val result = bundle.getInt("bundleKey")
            when(result){
                1 -> daystatus.setImageResource(R.drawable.one)
                2 -> daystatus.setImageResource(R.drawable.two)
                3 -> daystatus.setImageResource(R.drawable.three)
                4 -> daystatus.setImageResource(R.drawable.four)
                5 -> daystatus.setImageResource(R.drawable.five)
            }
        }*/

        adapter = CalendarAdapter(this)
        adapter.itemClickListener = object: CalendarAdapter.OnItemClickListener{

            override fun OnItemClick(
                holder: CalendarAdapter.CalViewHolder,
                view: View,
                position: Int
            ) {
                Toast.makeText(activity, position.toString(), Toast.LENGTH_SHORT).show()
                CalendarItemEditFragment().show(fragmentManager!!, "calendarFragFrag")
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

}
