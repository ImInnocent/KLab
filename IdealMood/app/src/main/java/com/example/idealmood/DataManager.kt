package com.example.idealmood

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemServiceName
import java.lang.Math.pow
import java.lang.Math.sqrt
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.pow

class DataManager private constructor() {
    var isStarted: Boolean = false
    var heartBeats: MutableList<Int> = mutableListOf<Int>() // 심박수를 10개씩 받아오기
    //var records: MutableList<String> = mutableListOf<String>()  // 그 중 6개만 string형태로 조합
    //var records: String = ""
    var lastHeartBeat: Int = 0
    private val myDBHelper = MyDBHelper.getInstance()!!
    private val rand: Random = Random(System.currentTimeMillis())
    var lastRage: Int = 0
    var todayRageTime: Int = 0 // 초단위
    var rmssd : Double = 0.0
    var cnt : Int = 1
    var SL: MutableList<Int> = mutableListOf<Int>()
    var todayRageAverage:Double = 0.0
    var pasttime = null
    var isHigh:Boolean = false

    init {
        // data auto generated
        if (AUTO_DATA) {
            isStarted = true

            val mainHandler = Handler(Looper.getMainLooper())

            mainHandler.post(object : Runnable {
                override fun run() {
                    addHeartBeat(generateArtificialHB())
                    mainHandler.postDelayed(this, AUTO_INTERVAL * 1000)
                }
            })
        }

        // 오늘의 분노 시간 계산 -> 이 객체 static이니까 여기다가 쓰면 한 번만 실행되겠지..?
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        val stressValues = myDBHelper.ST_findDataByDate(today)

        val format3 = SimpleDateFormat("HH:mm:ss")
        var pastStress :MyStress? = null
        var firstStress :MyStress? = null
        var lastStress :MyStress? = null
        for (value in stressValues) {
            // (분노 시간 구하기) stress 받은 기간 동안 시간 빼서 더하기
            if (value.stress >= SplashPageActivity.RAGE_POINT && pastStress != null && pastStress.stress < SplashPageActivity.RAGE_POINT) {
                firstStress = value
            }
            else if (value.stress < SplashPageActivity.RAGE_POINT && pastStress != null && pastStress.stress >= SplashPageActivity.RAGE_POINT) {
                lastStress = pastStress
                val firstTime = format3.parse(firstStress!!.date.split(" ")[1])
                val lastTime = format3.parse(lastStress.date.split(" ")[1])
                val diff = (firstTime.time - lastTime.time).toInt()
                todayRageTime += diff / 1000     // ms를 s로 환산
            }
            pastStress = value
        }
    }

    fun connect() {
        isStarted = true
    }

    fun disconnect() {
        isStarted = false
    }

    // 인공 스트레스 수치 생성
    fun generateArtificialRage(): Int {
        return AUTO_RAGE_MEDIAN + (rand.nextGaussian() * AUTO_RAGE_BOUND).toInt() - AUTO_RAGE_BOUND / 2
    }

    // 인공 심박수 생성
    fun generateArtificialHB(): Int {
        //return AUTO_MEDIAN + (rand.nextGaussian() * AUTO_BOUND).toInt() - AUTO_BOUND / 2
        return (60..70).random()
    }

    fun addHeartBeat(hb: Int) {
        if (!isStarted)
            return

        heartBeats.add(hb)
        lastHeartBeat = hb

        if (heartBeats.size >= MAX_COUNT) {
            var listForRecord = heartBeats.subList(0, DIVISION_COUNT)   // 전체 저장값중 6개만 자르기
            //records.add(listForRecord.joinToString())         // 레코드에 저장
            //records = listForRecord.joinToString()

            // 심박수가 COUNT이상 채워져서 records에 data가 넘어오면 DB에 저장하기
            var HBsum = 0
            for (i in listForRecord)
                HBsum += i
            myDBHelper.HB_insertData(HBsum / DIVISION_COUNT) // 심박수 분당 평균 DB에 저장

            listForRecord.clear()       // heartBeats에서 DIVISION_COUNT개 만큼 제거


        }

        setRage()
    }

    fun setRage() {
        // add algorithm here
        if(heartBeats.size == 3){
            var a : Double = 60000 / heartBeats[2].toDouble()
            var b : Double = 60000 / heartBeats[1].toDouble()
            var c : Double = 60000 / heartBeats[0].toDouble()
            rmssd = sqrt(((a - b) - (b - c)).pow(2) / 2)
            SL.add(calcSl(rmssd))
            lastRage = SL[0]
            ++cnt
        }
        else if(heartBeats.size > 3){
            rmssd = calcRmssd(rmssd)
            ++cnt
            SL.add(calcSl(rmssd))
            lastRage = SL[cnt - 2]
            //lastRage = rmssd.toInt()
        }else{
            lastRage = 0
        }
        Log.i("스트레스 수치", SL.joinToString())

        // DB에 저장
        myDBHelper.ST_insertData(lastRage)
        Log.i("마지막 스트레스", lastRage.toString())

        // delete this
        //val artiRage: Int  = generateArtificialRage()
        //lastRage = artiRage

        // 분노 상태인지 판단, 분노 시간 측정

        if (lastRage >= RAGE_POINT) {
            // TODO: Change interval
            if (AUTO_DATA) {    // 자동화한 경우
                todayRageTime += AUTO_INTERVAL.toInt()
                isHigh = true

            }
            else {  // 자동화가 아닌 경우
                todayRageTime += NOT_AUTO_INTERVAL.toInt()
            }
        }else{
            isHigh = false
        }
    }



    fun calcRmssd(prev : Double): Double{
        var n:Int = heartBeats.size
        var a: Double = 60000 / heartBeats[n - 1].toDouble()
        var b: Double = 60000 / heartBeats[n - 2].toDouble()
        var c: Double = 60000 / heartBeats[n - 3].toDouble()
        var temp: Double = ((a - b) - (b - c)).pow(2)
        var ret:Double = sqrt((prev * prev * (cnt - 1) + temp) / cnt)
        //Log.i("temp, prev", temp.toString() +", " + prev.toString())
        Log.i("rMSSD", ret.toString())

        return ret
    }

    fun calcSl(rmssd: Double): Int{
        var ret:Int = (2500 / (rmssd + 1)).toInt() + 30
        if (ret < 0 || ret > 100)
            ret = 0
        return ret
    }

    fun calcAverage(){

    }

    companion object {
        @Volatile private var instance: DataManager? = null

        @JvmStatic fun getInstance(): DataManager =
            instance ?: synchronized(this) {
                instance ?: DataManager().also {
                    instance = it
                }
            }

        // 자동으로 심박수 받아오지 않을 경우
        private const val NOT_AUTO_INTERVAL = 10 // 초 단위

        private const val AUTO_DATA: Boolean = false

        // 자동 심박수 정규 범위(넘을 수 잇음): MEDIAN - BOUND / 2 ~ MEDIAN + BOUND / 2
        private const val AUTO_MEDIAN: Int = 80
        private const val AUTO_BOUND: Int = 30
        private const val AUTO_INTERVAL: Long = 2 // 초 단위

        // 분노 시간: MEDIAN - BOUND / 2 ~ MEDIAN + BOUND / 2
        private const val AUTO_RAGE_MEDIAN: Int = 80
        private const val AUTO_RAGE_BOUND: Int = 30

        private const val MAX_COUNT: Int = 10      // 데이터 셋을 추출하는 기준
        private const val DIVISION_COUNT: Int = 6  // 데이터 셋의 크기 (10초 * 6 = 1분)

        // 분노 판단 역치값
        private const val RAGE_POINT = 75
    }
}