package com.example.idealmood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class emoTrashAdapter(val items:ArrayList<emoTrashData>)
    : RecyclerView.Adapter<emoTrashAdapter.MyemoTrashViewHolder>(){

    interface OnItemClickListener{
        fun OnItemClick(holder: MyemoTrashViewHolder, view: View, data:emoTrashData, position:Int)
    }

    var itemClickListener : OnItemClickListener?=null

    inner class MyemoTrashViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        var textTitleView: TextView = itemView.findViewById(R.id.rowTitle)
        var textDateView:TextView = itemView.findViewById(R.id.rowDate)
        init{
            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyemoTrashViewHolder {
        val v:View = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return MyemoTrashViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyemoTrashViewHolder, position: Int) {
        holder.textTitleView.text = items[position].title
        holder.textDateView.text = items[position].date
    }


}