package com.example.idealmood

import android.os.Handler
import android.os.Looper
import java.util.*

class DataManager private constructor() {
    var isStarted: Boolean = false
    var heartBeats: MutableList<Int> = mutableListOf<Int>() // 심박수를 10개씩 받아오기
    //var records: MutableList<String> = mutableListOf<String>()  // 그 중 6개만 string형태로 조합
    var records: String = ""
    var lastHeartBeat: Int = 0
    private val myDBHelper = MyDBHelper(GlobalContext.getContext())
    private val rand: Random = Random(System.currentTimeMillis())
    var lastRage: Int = 0
    var todayRageTime: Int = 0 // 초단위

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
    }

    fun connect() {
        isStarted = true
    }

    fun disconnect() {
        isStarted = false
    }

    // 인공 심박수 생성
    fun generateArtificialRage(): Int {
        return AUTO_RAGE_MEDIAN + (rand.nextGaussian() * AUTO_RAGE_BOUND).toInt() - AUTO_RAGE_BOUND / 2
    }

    // 인공 심박수 생성
    fun generateArtificialHB(): Int {
        return AUTO_MEDIAN + (rand.nextGaussian() * AUTO_BOUND).toInt() - AUTO_BOUND / 2
    }

    fun addHeartBeat(hb: Int) { // true : records로 data가 넘어갔을 때
        if (!isStarted)
            return

        heartBeats.add(hb)
        lastHeartBeat = hb

        if (heartBeats.size >= MAX_COUNT) {
            var listForRecord = heartBeats.subList(0, DIVISION_COUNT)   // 전체 저장값중 6개만 자르기
            //records.add(listForRecord.joinToString())         // 레코드에 저장
            records = listForRecord.joinToString()

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

        // delete this
        val artiRage: Int  = generateArtificialRage()
        lastRage = artiRage

        if (artiRage >= RAGE_POINT) {
            // TODO: Change interval
            todayRageTime += AUTO_INTERVAL.toInt()
        }
    }



    companion object {
        @Volatile private var instance: DataManager? = null

        @JvmStatic fun getInstance(): DataManager =
            instance ?: synchronized(this) {
                instance ?: DataManager().also {
                    instance = it
                }
            }

        private const val AUTO_DATA: Boolean = true

        // 자동 심박수 정규 범위(넘을 수 잇음): MEDIAN - BOUND / 2 ~ MEDIAN + BOUND / 2
        private const val AUTO_MEDIAN: Int = 80
        private const val AUTO_BOUND: Int = 30
        private const val AUTO_INTERVAL: Long = 2 // 초 단위

        // 분노 시간: MEDIAN - BOUND / 2 ~ MEDIAN + BOUND / 2
        private const val AUTO_RAGE_MEDIAN: Int = 80
        private const val AUTO_RAGE_BOUND: Int = 30

        private const val MAX_COUNT: Int = 10      // 데이터 셋을 추출하는 기준
        private const val DIVISION_COUNT: Int = 6  // 데이터 셋의 크기 (10초 * 6 = 1분)

        // 기준 분노 수치
        private const val RAGE_POINT = 75
    }
}