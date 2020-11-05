package com.example.idealmood

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val items: Array<String>)
    : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    interface OnItemClickListener{  // main에서 구현하기 위한 클릭이벤트
        fun OnItemClick(holder: MyViewHolder, view: View, data: String, position: Int)
    }

    var itemClickListener: OnItemClickListener ?= null
    val imgArr = arrayOf<Int>(R.drawable.meditation, R.drawable.deepbreath, R.drawable.exercise,
                                            R.drawable.kpopdance, R.drawable.asmr, R.drawable.rain,
                                            R.drawable.ocean) // data 추가 예정

    inner class MyViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var solutionTxt: TextView = itemView.findViewById(R.id.solutionText)    // 솔루션 하나하나 버튼
        var solutionImage: ImageView = itemView.findViewById(R.id.solutionImg)
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
        holder.solutionTxt.text = items[position]
        holder.solutionImage.setImageResource(imgArr[position])
    }

}