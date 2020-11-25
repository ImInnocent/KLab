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
import kotlin.math.roundToInt

class StatisticsFragment : Fragment() {

    companion object {
        val DAY_PER_WEEK = 7
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val myDBHelper = MyDBHelper.getInstance()!!
        val dataObjects:Array<Float> = Array(DAY_PER_WEEK, { i -> i.toFloat()})
        val SLlists :MutableList<Entry> = ArrayList<Entry>()
        val HBlists = ArrayList<Entry>()

        // Stress Level 값 받아오기
        for ( i in dataObjects) {

            //dataObject객체를 내 타입(List타입) 객체로 변환
            //list.add(new Entry(data.getValueX(), data.getValueY())
            SLlists.add(Entry(i.toFloat(), (Math.random() * 40 + 30).toFloat()))

        }

        // Heart Rate 값 받아오기
        // 1. 지난 일주일간 값 저장하는 배열 만들기
        val sevenDays = ArrayList<String>()
        val current = LocalDate.now()
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
                HBlists.add(Entry(idx.toFloat(), (sum / size).toFloat()))   // 소수점 없이 그냥 출력
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
        // 일단 가라로 넣음. 나중에 수정 예정
        var analysis = ""
        analysis += "전체 스트레스 평균 : ${((Math.random() * 40 + 30).toFloat() * 1000).roundToInt() / 1000.0}\n"
        var ssum = 0.0
        for (data in SLlists) {
            ssum += data.y
        }
        analysis += "최근 일주일 간 스트레스 평균 : ${(ssum * 1000 / 7).roundToInt() / 1000.0}\n"
        analysis += "오늘 스트레스는 상위 ${(20..50).random()}% 입니다.\n\n"
        analysis += "추천 솔루션 :\n"
        analysis += "클래식 음악 듣기, 명상하기\n"
        emotionAnalysis.text = analysis
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_statistics, container, false)
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
