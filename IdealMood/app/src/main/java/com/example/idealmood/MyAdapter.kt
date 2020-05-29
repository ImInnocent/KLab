package com.example.idealmood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val items: Array<String?>)
    : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    interface OnItemClickListener{  // main에서 구현하기 위한 클릭이벤트
        fun OnItemClick(holder: MyViewHolder, view: View, data: String?, position: Int)
    }

    var itemClickListener: OnItemClickListener ?= null

    inner class MyViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var solution: TextView = itemView.findViewById(R.id.solutionBtn)    // 솔루션 하나하나 버튼
        init{
            itemView.setOnClickListener{
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.solutions, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        holder.solution.text = items[position]
    }

}