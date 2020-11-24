package com.example.idealmood

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*

class CalendarAdapter(val frag:CalendarFragment, val myDBHelper: MyDBHelper) : RecyclerView.Adapter<CalendarAdapter.CalViewHolder>() {

    companion object {  // 이미지 배열
        val EMOJI:Map<Int, Int> = mapOf(
            Pair(1, R.drawable.emotion1_face),
            Pair(2, R.drawable.emotion2_face),
            Pair(3, R.drawable.emotion3_face),
            Pair(4, R.drawable.emotion4_face),
            Pair(5, R.drawable.emotion5_face)
        )
    }

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
        fun OnItemClick(holder: CalViewHolder, view: View,  position:Int, baseCalendar: BaseCalendar)
    }

    var itemClickListener : OnItemClickListener?=null


    inner class CalViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        var item_date = containerView.findViewById<TextView>(R.id.item_date)
        var item_image = containerView.findViewById<ImageView>(R.id.daystatus)
        // 해당 grid에 해당하는 날짜

        init{
            itemView.setOnClickListener {
                // 해당 달 안에 속한 날들일 때만 클릭리스너 구현하기
                if(adapterPosition >= baseCalendar.prevMonthTailOffset &&
                    adapterPosition < baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate)
                    itemClickListener?.OnItemClick(this, it, adapterPosition, baseCalendar)
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
        // 일요일을 빨갛게 설정하기
        if (position % BaseCalendar.DAYS_OF_WEEK == 0) holder.item_date.setTextColor(Color.parseColor("#ff1200"))
        else holder.item_date.setTextColor(Color.parseColor("#676d6e"))

        if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            holder.item_date.alpha = 0.3f
        } else {
            holder.item_date.alpha = 1f
            //여기서 database검사해서 전체 달력에 보여주는 쿼리 작성. -> 현재 달(불투명한 것)에 속하는 월만 표시
            // DB에 해당 날짜가 있는지 검색 있으면 반환하겠죠?
            val today = frag.currMonth.text.toString() + " " + baseCalendar.data[position].toString()
            val data = myDBHelper.CD_findOneData(today)
            if(data.emotion != 0)   // data가 검색이 되면
                EMOJI[data.emotion]?.let { holder.item_image.setImageResource(it) }    // 해당 emotion 이미지 적용
        }
        holder.item_date.text = baseCalendar.data[position].toString()
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