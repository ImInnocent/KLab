package com.example.idealmood

class DataManager private constructor() {
    var isStarted: Boolean = false
    var heartBeat: Int = 100
    var heartBeats: MutableList<Int> = mutableListOf<Int>() // 심박수를 10개씩 받아오기
    var records: MutableList<String> = mutableListOf<String>()  // 그 중 6개만 string형태로 조합

    public fun addHeartBeat(hb: Int) {
        heartBeats.add(hb)
        if (heartBeats.size >= MAX_COUNT) {
            var listForRecord = heartBeats.subList(0, DIVISION_COUNT)   // 전체 저장값중 6개만 자르기
            records.add(listForRecord.joinToString())         // 레코드에 저장
            listForRecord.clear()       // heartBeats에서 DIVISION_COUNT개 만큼 제거
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

        private const val MAX_COUNT: Int = 10      // 데이터 셋을 추출하는 기준
        private const val DIVISION_COUNT: Int = 6  // 데이터 셋의 크기 (10초 * 6 = 1분)
    }
}