package com.example.idealmood

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RvAdapter(val context: Context,
                private val list: ArrayList<BluetoothDevice>,
                private val itemClick: (BluetoothDevice) -> Unit)
    : RecyclerView.Adapter<RvAdapter.Holder>() {

    // 뷰 홀더 생성하는 코드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.device_scan_rv_item, parent, false)
        return Holder(view, itemClick)
    }

    // 현재 리사이클 뷰 안에 있는 아이템 갯수
    override fun getItemCount(): Int {
        return list.size
    }

    // ??
    override fun onBindViewHolder(holder: RvAdapter.Holder, position: Int) {
        holder?.bind(list[position], context)
    }

    // 홀더
    inner class Holder(itemView: View, itemClick: (BluetoothDevice) -> Unit)
        : RecyclerView.ViewHolder(itemView) {
        // get 텍스트
        val text: TextView = itemView?.findViewById<TextView>(R.id.deviceText)

        // 아이템에서 정보를 추출해서 한 줄을 구성하기
        fun bind (device: BluetoothDevice, context: Context) {
            if (device.name != null) {
                text.text = device.name
            } else {
                text.text = device.address
            }

            itemView.setOnClickListener { itemClick(device) }
        }
    }
}