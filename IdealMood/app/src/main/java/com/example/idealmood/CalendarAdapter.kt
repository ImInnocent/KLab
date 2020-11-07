package com.example.idealmood

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import java.util.*

class CalendarAdapter(val frag:CalendarFragment) : RecyclerView.Adapter<CalendarAdapter.CalViewHolder>() {

    val baseCalendar = BaseCalendar()

    init {
        baseCalendar.initBaseCalendar {
            refreshView(it)
        }
    }

    private fun refreshView(calendar: Calendar) {
        notifyDataSetChanged()
        frag.refreshCurrentMonth(calendar)
    }

    interface OnItemClickListener{
        fun OnItemClick(holder: CalViewHolder, view: View,  position:Int)
    }

    var itemClickListener : OnItemClickListener?=null


    inner class CalViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        var item_date = containerView.findViewById<TextView>(R.id.item_date)

        init{
            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return CalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onBindViewHolder(holder: CalViewHolder, position: Int) {
        if (position % BaseCalendar.DAYS_OF_WEEK == 0) holder.item_date.setTextColor(Color.parseColor("#ff1200"))
        else holder.item_date.setTextColor(Color.parseColor("#676d6e"))

        if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            holder.item_date.alpha = 0.3f
        } else {
            holder.item_date.alpha = 1f
        }
        holder.item_date.text = baseCalendar.data[position].toString()

        //여기서 database검사해서 전체 달력에 보여주는 쿼리 작성.

    }

    fun changeToPrevMonth() {
        baseCalendar.changeToPrevMonth {
            refreshView(it)
        }
    }

    fun changeToNextMonth() {
        baseCalendar.changeToNextMonth {
            refreshView(it)
        }
    }



}