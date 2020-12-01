package com.example.idealmood

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class StatisticsFragment : Fragment() {

    val myDBHelper = MyDBHelper.getInstance()!!
    //val dataObjects:Array<Float> = Array(DAY_PER_WEEK, { i -> i.toFloat()})
    val SLlists :MutableList<Entry> = ArrayList<Entry>()
    val HBlists = ArrayList<Entry>()

    companion object {
        val DAY_PER_WEEK = 7
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }


    private fun init() {
        // Stress Level 값 받아오기
        // 지난 6일간 스트레스 평균 받아와서 list에 저장
        val sixDays = ArrayList<String>()     // 지난 6일간 값 저장하는 배열
        val current = LocalDate.now()
        for(i in DAY_PER_WEEK - 1 downTo 1)
            sixDays.add(current.minusDays(i.toLong()).format(DateTimeFormatter.ofPattern("yyyy MM d")))
        for ((idx, date) in sixDays.withIndex()) {
            val averageStress = myDBHelper.CD_findOneData(date).averagestress
            if (averageStress != -1.0)    // 검색이 된 경우
                SLlists.add(Entry(idx.toFloat(), myDBHelper.CD_findOneData(date).averagestress.toFloat()))
            else {  // 검색이 안된 경우
                SLlists.add(Entry(idx.toFloat(), 0.0f))
            }
        }

        // 오늘 스트레스 평균 받아와서 list에 저장
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        val stressValues = myDBHelper.ST_findDataByDate(today)
        var todayStressSum = 0.0

        for (value in stressValues) {
            todayStressSum += value.stress
        }
        val stressSize = stressValues.size
        val todayAverageStress = when {
            stressSize > 0 -> todayStressSum / stressSize
            else -> 0.0
        }
        SLlists.add(Entry((DAY_PER_WEEK - 1).toFloat(), todayAverageStress.toFloat()))
        //dataObject객체를 내 타입(List타입) 객체로 변환
        //list.add(new Entry(data.getValueX(), data.getValueY())
        //SLlists.add(Entry(i.toFloat(), (Math.random() * 40 + 30).toFloat()))


        // Heart Rate 값 받아오기
        // 1. 지난 일주일간 값 저장하는 배열 만들기
        val sevenDays = ArrayList<String>()
        for(i in DAY_PER_WEEK - 1 downTo 0)
            sevenDays.add(current.minusDays(i.toLong()).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
        // 2. 적용하여 값 가져오고, 평균 내서 list에 저장
        for ((idx, date) in sevenDays.withIndex()) {
            val heartbeats = myDBHelper.HB_findDataByDate(date)
            var sum = 0
            for (heartbeat in heartbeats)
                sum += heartbeat
            val size = heartbeats.size
            Log.i("심박 기록 데이터 수", "($date) $size")
            if(size > 0)
                HBlists.add(Entry(idx.toFloat(), (sum.toFloat() / size)))
            else
                HBlists.add(Entry(idx.toFloat(), 0.0f))
        }


        val xAxis = lineChart.xAxis //lineChart의 x축
        xAxis.apply{

            setDrawGridLines(false)

        }

        lineChart.apply {
            isEnabled = false
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f

        }


        // SL 수치 그래프
        //add "lists" to dataset
        var SLdataSet = LineDataSet(SLlists, getString(R.string.statistics_legend1))
        //dataSet.setValueTextColor();
        SLdataSet.apply {
            axisDependency = YAxis.AxisDependency.LEFT //y값 데이터 왼쪽
            color = resources.getColor(R.color.CDarkGreen) //라인 색 지정
            setCircleColor(resources.getColor(R.color.CDarkGreen))
            lineWidth = 2f
            circleRadius = 3f
            fillAlpha = 0
            setDrawValues(true) //값 명시.
        }

        // 심박수 수치 그래프
        var HBdataSet = LineDataSet(HBlists, getString(R.string.statistics_legend2))
        //dataSet.setValueTextColor();
        HBdataSet.apply {
            axisDependency = YAxis.AxisDependency.LEFT //y값 데이터 왼쪽
            color = resources.getColor(R.color.CCoral) //라인 색 지정
            setCircleColor(resources.getColor(R.color.CCoral))
            lineWidth = 2f
            circleRadius = 3f
            fillAlpha = 0
            setDrawValues(true) //값 명시.
        }

        val dataSets = listOf(SLdataSet, HBdataSet)
        var lineData:LineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.invalidate()


        //분석 텍스트박스 : id/emotionAnalysis
        // 이번엔 가라 아니고 진짜임^^
        // 전체 스트레스 평균 구하기 (+ 총 분노 시간도 구하기)
        myDBHelper.CD_getAllRecord()    // 전체 값 받아오기
        var totalStressSum = 0.0
        var totalRageTime = 0.0
        val stressArray = ArrayList<Double>()   // 나중에 상위 stress 수치룰 구할, 스트레스 저장 배열

        for (calendar in myDBHelper.CDArray.dropLast(1)) {  // 오늘 제외하교 평균 다 더하기
            totalStressSum += calendar.averagestress
            stressArray.add(calendar.averagestress)
            totalRageTime += calendar.ragetime
        }
        totalStressSum += todayAverageStress   // 오늘 값 더하기
        stressArray.add(todayAverageStress)

        val totalAverageStress = totalStressSum / (myDBHelper.CDArray.size)

        // 말 넣기
        var analysis = ""
        // 전체 스트레스 평균
        analysis += getString(R.string.statistics_total_stress_average) + " " + totalAverageStress + "\n"
        // 최근 일주일 간 스트레스 평균
        var ssum = 0.0
        for (data in SLlists) {
            ssum += data.y
        }
        analysis += getString(R.string.statistics_7days_stress_average) + " " + (ssum / DAY_PER_WEEK) + "\n"

        // 스트레스 상위 몇퍼인지 구하기
        stressArray.sort()   // 오름차순 정렬
        val rank = stressArray.indexOf(todayAverageStress)  // 등수 구하기
        analysis += getString(R.string.statistics_today_stress_rank_first) + " " +
                (rank.toFloat() / stressArray.size * 100.0f) +
                getString(R.string.statistics_today_stress_rank_last) + "\n\n"

        // 지난 6일간 총 분노 시간 구하기
        analysis += getString(R.string.statistics_total_rage_time) + " " + totalRageTime / 60 + " "+
                getString(R.string.statistics_min) + "\n"

        // 추천 솔루션
        analysis += getString(R.string.statistics_recommended_solution_title) + "\n"
        analysis += getString(R.string.statistics_recommended_solution_contents) + "\n"
        emotionAnalysis.text = analysis
    }

    //[Data 표시]
    //1일 단위로 갱신되는 평균 수치를 그래프로 찍도록 함.
    //한눈에 볼 수 있는 data 갯수는 일당 평균값 일주일치(7개) 정도.
    //따라서 가로축은 변수 value는 표시되지 않도록 하고 세로축은 0~100범위로 표시


    //[분석 결과 표시]
    //분석 결과는 연령에 따라 개인화된 추천 정보를 제시한다.
    //ex) 50대 남성의 경우에는 심박수의 급격한 변화에 따라 질환 주의 멘트를 넣는다.
    //분석결과에 나타낼 정보는
    //전체 스트레스 평균
    //최근 일주일간 스트레스 평균
    //오늘의 스트레스 평균이 평소의 상위 몇퍼센트인지
    //추천 솔루션
    //그 외, 추가하고싶은거 있으면 주석에 멘트로 먼저 추가바람


    //[스트레스 수치 temp]
    //10초마다 측정된다고 하면 오늘부터 지금까지의 측정횟수 : n회 지금까지의 평균 : m
    //현재 측정 값 : a    -> 새로운 측정횟수 : n + 1 / 지금까지의 평균 : ((n * m) + a)/(n + 1)로 갱신
    //데이터베이스에 평균 값, 임계 넘은 횟수 이렇게 하루 2개씩 저장됨
    //임계넘은 횟수 정의 : 임계 수치(_ex]전체 평균스트레스 수치의 120%로 정한다고 하자.) 를 넘기 시작한 순간부터 임계수치보다 낮아지기까지의 구간을 포함해 1회로 한다.
    //단 구간의 지속 시간이 너무 짧을 시 포함하지 않는걸로 횟수가 필요이상으로 많이 측정되는 상황을 피한다.
    

}
