package com.example.idealmood

import android.graphics.Color
import android.os.Bundle
import android.util.Half.toFloat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.example.idealmood.R.id
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils.init
import kotlinx.android.synthetic.main.fragment_statistics.*

class StatisticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val dataObjects:Array<Float> = Array(5, {i -> i.toFloat()})
        var lists:MutableList<Entry> = ArrayList<Entry>();
        for( i in dataObjects){

            //dataObject객체를 내 타입(List타입) 객체로 변환
            //list.add(new Entry(data.getValueX(), data.getValueY())
            lists.add(Entry(i.toFloat(), (Math.random() * 10).toFloat()))

        }

        //add "lists" to dataset
        var dataSet = LineDataSet(lists, "DataSet 1")
        dataSet.color = Color.BLUE
        //dataSet.setValueTextColor();

        if(dataSet == null){
            System.out.println("dataSet은 NULL이래")
        }else {
            System.out.println("dataSet NULL 아니래")
        }

        var lineData:LineData = LineData(dataSet)
        
        if(lineData ==null) {
            System.out.println("lineData는 NULL이래")
        }else{
            System.out.println("lineData NULL아니래")
        }

        //lineData에 값이 없다고 나오는데?? -> logcat찍어보니까 또  NULL은 아니라고 나오는데 머야
        lineChart?.data = lineData
        lineChart?.invalidate()

        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }




}
