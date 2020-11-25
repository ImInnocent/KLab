package com.example.idealmood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SplashPageActivity : AppCompatActivity() {

    companion object {
        const val RAGE_POINT = 75
    }

    lateinit var myDBHelper :MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_page)

        // 첫 실행되는 액티비티에 있어야 합니다.
        // 시작 엑티비티가 바뀌면 이 코드도 옮겨주세요
        GlobalContext.setContext(this)

        setData()

        Handler().postDelayed({
            val intent: Intent = if (UserInfo.has(UserInfo.NAME_PASSED) && UserInfo.get(UserInfo.NAME_PASSED) == true) {
                Intent(this,  MainActivity::class.java)
            } else {
                Intent(this,  InitNameActivity::class.java)
            }

            startActivity(intent)
        }, 2000)
    }

    private fun setData() {
        // 어제자 Stress Level 평균, 분노 시간 갱신
        myDBHelper = MyDBHelper.getInstance()!!
        val yesterday = LocalDate.now().minusDays(1)
        val format1 = DateTimeFormatter.ofPattern("yyyy MM d")
        val format2 = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        if(myDBHelper.CD_findOneData(yesterday.format(format1)).ragetime == -1) {   // 아직 기록 안했을 때
            // 평균 stress 값, 분노 시간 받아오기
            val stressValues = myDBHelper.ST_findDataByDate(yesterday.format(format2))
            var average = 0.0
            var rageTime = 0

            var sum = 0.0
            val format3 = SimpleDateFormat("HH:mm:ss")
            var pastStress :MyStress? = null
            var firstStress :MyStress? = null
            var lastStress :MyStress? = null
            for (value in stressValues) {
                // (평균 s.l 구하기) 스트레스 수치 다 합하기
                sum += value.stress

                // (분노 시간 구하기) stress 받은 기간 동안 시간 빼서 더하기
                if (value.stress >= RAGE_POINT && pastStress != null && pastStress.stress < RAGE_POINT) {
                    firstStress = value
                }
                else if (value.stress < RAGE_POINT && pastStress != null && pastStress.stress >= RAGE_POINT) {
                    lastStress = pastStress
                    val firstTime = format3.parse(firstStress!!.date.split(" ")[1])
                    val lastTime = format3.parse(lastStress.date.split(" ")[1])
                    val diff = (firstTime.time - lastTime.time).toInt()
                    rageTime += diff / 1000     // ms를 s로 환산
                }
                pastStress = value
            }
            // (평균 s.l 구하기) 합을 size로 나눠 평균내기
            val size = stressValues.size
            if(size > 0)
                average = sum / size

            // DB에 받아온 값 update 하기
            if(myDBHelper.CD_findOneData(yesterday.format(format1)).emotion == 0) {   // 캘린더에 등록 안한 경우
                val emotion = when (average.toInt() / 10) { // 자동으로 표정 판단해주기^^ 와 너무 좋다^^
                    2 -> 5
                    3 -> 5
                    4 -> 4
                    5 -> 3
                    6 -> 3
                    7 -> 2
                    8 -> 1
                    9 -> 1
                    else -> 3
                }
                myDBHelper.CD_insertData(MyCalendar(emotion, average, rageTime, yesterday.format(format1)))
            }
            else    // 이미 있는 경우 update
                myDBHelper.CD_updateStressData(average, rageTime, yesterday.format(format1))
            Log.i("어제 SL, 분노 시간 값", "$average, $rageTime")
        }
    }
}
