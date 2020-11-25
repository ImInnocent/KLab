package com.example.idealmood

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyDBHelper(val context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        val DB_VERSION = 1
        val DB_NAME = "idealmood_record.db"

        // 심박수 테이블
        val HB_TABLE_NAME = "heartbeat" // 테이블명
        val HB_PID = "pid"  // 고유 id
        val HB_PHEARTBEAT = "pheartbeat"    // 심박수
        val HB_PDATE = "pdate"  // 날짜

        // 스트레스 테이블
        val ST_TABLE_NAME = "stress" // 테이블명
        val ST_PID = "pid"  // 고유 id
        val ST_PSTRESS = "pstress"    // Stress Level
        val ST_PDATE = "pdate"  // 날짜

        //캘린더 테이블
        val CD_TABLE_NAME = "calendar"
        val CD_PID = "pid"
        val CD_PEMOTION = "pemotion"    // 기분 상태 (1~5까지 숫자로 받음)
        val CD_PAVERAGESTRESS = "paveragestress"    // 일일 평균 스트레스 수치
        val CD_PRAGETIME = "pragetime"  // 분노 시간 (s)
        val CD_PDATE = "pdate"  // 기록 날짜 (yyyy MM (d)d) 형태

        // 감쓰 테이블
        val ET_TABLE_NAME = "emotrash"
        val ET_PID = "pid"
        val ET_PTEXT = "ptext"  // 감정일기 내용
        val ET_PDATE = "pdate"  // 기록 날짜
        val ET_PISDELETE = "pisdelete"  // 감쓰쓰에 들어갔는지 여부

        // static 객체 생성을 위한 static 함수 선언
        var myDBHelper :MyDBHelper? = null
        fun getInstance() :MyDBHelper? {
            if(myDBHelper == null)
                myDBHelper = MyDBHelper(GlobalContext.getContext())
            return myDBHelper
        }
    }

    val HBArray = ArrayList<Int>()
    val CDArray = ArrayList<MyCalendar>()
    val ETArray = ArrayList<emoTrashData>()
    val ETArray_Del = ArrayList<emoTrashData>() // 감쓰쓰 저장 배열

    override fun onCreate(db: SQLiteDatabase?) {
        // 심박수 테이블 생성
        val create_HB_table = "create table if not exists $HB_TABLE_NAME (" +
                "$HB_PID integer primary key autoincrement, " +
                "$HB_PHEARTBEAT integer, " +
                "$HB_PDATE text)"   // autoincrement : 자동 증가
        db?.execSQL(create_HB_table)    // db 실행, select 구문 제외 insert, delete 등등 실행 가능

        // 스트레스 테이블 생성
        val create_ST_table = "create table if not exists $ST_TABLE_NAME (" +
                "$ST_PID integer primary key autoincrement, " +
                "$ST_PSTRESS real, " +
                "$ST_PDATE text)"
        db?.execSQL(create_ST_table)

        // 캘린더 테이블 생성
        val create_CD_table = "create table if not exists $CD_TABLE_NAME (" +
                "$CD_PID integer primary key autoincrement, " +
                "$CD_PEMOTION integer, " +
                "$CD_PAVERAGESTRESS real, " +
                "$CD_PRAGETIME integer, " +
                "$CD_PDATE text)"
        db?.execSQL(create_CD_table)

        // 감쓰 테이블 생성
        val create_ET_table = "create table if not exists $ET_TABLE_NAME (" +
                "$ET_PID integer primary key autoincrement, " +
                "$ET_PTEXT text, " +
                "$ET_PDATE text, " +
                "$ET_PISDELETE integer)"
        db?.execSQL(create_ET_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 버전 정보가 바뀌었을 때
        val drop_HB_table = "drop table if exists $HB_TABLE_NAME"
        db?.execSQL(drop_HB_table) // dp 드랍하고
        onCreate(db)    // 다시 만들기

        val drop_ST_table = "drop table if exists $ST_TABLE_NAME"
        db?.execSQL(drop_ST_table) // dp 드랍하고
        onCreate(db)    // 다시 만들기

        val drop_CD_table = "drop table if exists $CD_TABLE_NAME"
        db?.execSQL(drop_CD_table)
        onCreate(db)

        val drop_ET_table = "drop table if exists $ET_TABLE_NAME"
        db?.execSQL(drop_ET_table)
        onCreate(db)
    }


    /////// 심박수 테이블 관련 함수 ///////
    fun HB_insertData(heartbeat :Int) :Boolean { // INSERT, 삽입 성공 여부
        val nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
        val values = ContentValues()
        values.put(HB_PHEARTBEAT, heartbeat)
        values.put(HB_PDATE, nowDate)
        val db=this.writableDatabase    // DB table 객체 획득
        return if(db.insert(HB_TABLE_NAME, null, values) > 0) {
            Log.i("심박 데이터 삽입", heartbeat.toString())
            db.close()
            true
        } else {
            db.close()
            false
        }
    }

    // 쓸 일이 있을까? 싶지만 만들어보는 심박수 데이터 삭제 함수 (날짜 기준)
    fun HB_deleteData(pdate :String) :Boolean {
        val strsql = "select * from $HB_TABLE_NAME where $HB_PDATE = \'$pdate\'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(cursor.count != 0) { // 무언가를 가지고 옴
            db.delete(HB_TABLE_NAME, "$HB_PDATE =? ", arrayOf(pdate)) // or PIR + " = " + pid
            cursor.close()
            db.close()
            val activity = context as Activity
            // activity 내용 반영
            return true
        }
        else {
            cursor.close()
            db.close()
            return false
        }
    }

    fun HB_findDataByDate(pdate: String) :ArrayList<Int> {    // pdate : 시간 제외 날짜만. 날짜에 해당하는 모든 심박수
        val strsql = "select * from $HB_TABLE_NAME where $HB_PDATE like '$pdate%'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val heartRates = ArrayList<Int>()
        if(cursor.count != 0) { // 무언가를 가지고 옴
            cursor.moveToFirst()
            do {
                heartRates.add(cursor.getInt(1))
            } while(cursor.moveToNext())
            cursor.close()
            db.close()
        }
        else {
            cursor.close()
            db.close()
        }
        return heartRates
    }

    fun HB_getAllRecord() {    // test용 함수 -. Data 제대로 들어갔는지
        HBArray.clear()
        val strsql = "select * from $HB_TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
        if (cursor.count != 0) { // 무언가를 가지고 옴
            HB_getOneRecord(cursor)
        }
        cursor.close()
        db.close()
    }

    private fun HB_getOneRecord(cursor :Cursor) {
        cursor.moveToFirst()
        do {
            HBArray.add(cursor.getInt(1))
            Log.i("심박수 데이터 목록 : ", cursor.getInt(1).toString())
        } while(cursor.moveToNext())
    }


    /////// 스트레스 테이블 관련 함수 ///////
    fun ST_insertData(stress :Int) :Boolean { // INSERT, 삽입 성공 여부
        val nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
        val values = ContentValues()
        values.put(ST_PSTRESS, stress)
        values.put(ST_PSTRESS, nowDate)
        val db=this.writableDatabase    // DB table 객체 획득
        return if(db.insert(ST_TABLE_NAME, null, values) > 0) {
            db.close()
            true
        } else {
            db.close()
            false
        }
    }

    fun ST_findDataByDate(pdate: String) :ArrayList<MyStress> {    // pdate : 시간 제외 날짜만. 날짜에 해당하는 모든 S.L
        val strsql = "select * from $ST_TABLE_NAME where $ST_PDATE like '$pdate%'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
        val stressLevels = ArrayList<MyStress>()
        if(cursor.count != 0) { // 무언가를 가지고 옴
            cursor.moveToFirst()
            do {
                stressLevels.add(MyStress(cursor.getDouble(1), cursor.getString(2)))
            } while(cursor.moveToNext())
            cursor.close()
            db.close()
        }
        else {
            cursor.close()
            db.close()
        }
        return stressLevels
    }


    ///// 캘린더 테이블 관련 함수 /////
    fun CD_insertData(calendar: MyCalendar) :Boolean {
        val values = ContentValues()
        values.put(CD_PEMOTION, calendar.emotion)
        values.put(CD_PAVERAGESTRESS, calendar.averagestress)
        values.put(CD_PRAGETIME, calendar.ragetime)
        values.put(CD_PDATE, calendar.date)
        val db=this.writableDatabase    // DB table 객체 획득
        return if(db.insert(CD_TABLE_NAME, null, values) > 0) { // insert가 제대로 안 되었을 경우 -1 반환
            db.close()
            true
        } else {
            db.close()
            false
        }
    }

    fun CD_deleteData(pdate :String) :Boolean {
        val strsql = "select * from $CD_TABLE_NAME where $CD_PDATE = \'$pdate\'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(strsql, null)

        if(cursor.count != 0) { // 무언가를 가지고 옴
            db.delete(CD_TABLE_NAME, "$CD_PDATE =? ", arrayOf(pdate)) // or PIR + " = " + pid
            cursor.close()
            db.close()
            return true
        }
        else {
            cursor.close()
            db.close()
            return false
        }
    }

    fun CD_updateEmotionData(pemotion :Int, pdate :String) :Boolean {  // 감정 수정하기
        val strsql = "select * from $CD_TABLE_NAME where $CD_PDATE = '$pdate'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(cursor.moveToFirst()) { // 무언가를 가지고 옴
            val values = ContentValues()
            values.put(CD_PEMOTION, pemotion)    // 바꾸고자 하는 내용
            db.update(CD_TABLE_NAME, values, "$CD_PDATE=?", arrayOf(pdate))
            cursor.close()
            db.close()
            return true
        }
        else {
            cursor.close()
            db.close()
            return false
        }
    }

    fun CD_updateStressData(paveragestress :Double, pragetime :Int, pdate :String) :Boolean {  // 나머지(SL, 분노시간) 수정하기
        val strsql = "select * from $CD_TABLE_NAME where $CD_PDATE = '$pdate'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(cursor.moveToFirst()) {
            val values = ContentValues()
            values.put(CD_PAVERAGESTRESS, paveragestress)    // 바꾸고자 하는 내용
            values.put(CD_PRAGETIME, pragetime)
            db.update(CD_TABLE_NAME, values, "$CD_PDATE=?", arrayOf(pdate))
            cursor.close()
            db.close()
            return true
        }
        else {
            cursor.close()
            db.close()
            return false
        }
    }

    fun CD_findOneData(pdate: String) :MyCalendar {
        val strsql = "select * from $CD_TABLE_NAME where $CD_PDATE = '$pdate'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(cursor.count != 0) { // 무언가를 가지고 옴
            cursor.moveToFirst()
            val data = MyCalendar(cursor.getInt(1), cursor.getDouble(2),
                                    cursor.getInt(3), cursor.getString(4))
            cursor.close()
            db.close()
            return data
        }
        else {
            cursor.close()
            db.close()
            return MyCalendar(0, -1.0, -1, "")
        }
    }

    fun CD_getAllRecord() {
        CDArray.clear()
        val strsql = "select * from $CD_TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
        if (cursor.count != 0) { // 무언가를 가지고 옴
            CD_getOneRecord(cursor)
        }
        cursor.close()
        db.close()
    }

    private fun CD_getOneRecord(cursor :Cursor) {
        cursor.moveToFirst()
        do {
            CDArray.add(MyCalendar(cursor.getInt(1), cursor.getDouble(2),
                                            cursor.getInt(3), cursor.getString(4)))
            // 실제 캘린더뷰에 갱신
            Log.i("캘린더 데이터 목록 : ", cursor.getInt(1).toString())
        } while(cursor.moveToNext())
    }


    /////// 감정 일기 관련 함수 ///////

    fun ET_insertData(data: emoTrashData) :Boolean {
        var isDeleteInt :Int = 0
        if (data.isDelete)  // true일 때
            isDeleteInt = 1
        else
            isDeleteInt = -1

        val values = ContentValues()
        values.put(ET_PTEXT, data.title)
        values.put(ET_PDATE, data.date)
        values.put(ET_PISDELETE, isDeleteInt)
        val db=this.writableDatabase    // DB table 객체 획득
        return if(db.insert(ET_TABLE_NAME, null, values) > 0) { // insert가 제대로 안 되었을 경우 -1 반환
            db.close()
            true
        } else {
            db.close()
            false
        }
    }

    fun ET_deleteData(data :emoTrashData) :Boolean {
        val strsql = "select * from $ET_TABLE_NAME where $ET_PTEXT = '${data.title}' and $ET_PDATE = '${data.date}'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(strsql, null)

        if(cursor.count != 0) { // 무언가를 가지고 옴
            db.delete(ET_TABLE_NAME, "$ET_PTEXT=? and $ET_PDATE=?", arrayOf(data.title, data.date))
            cursor.close()
            db.close()
            return true
        }
        else {
            cursor.close()
            db.close()
            return false
        }
    }

    fun ET_updateData(data :emoTrashData) :Boolean {  // isDelete수정 (감쓰->감쓰쓰)
        val strsql = "select * from $ET_TABLE_NAME where $ET_PTEXT = '${data.title}' and $ET_PDATE = '${data.date}'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(strsql, null)
        if(cursor.moveToFirst()) { // 무언가를 가지고 옴
            val values = ContentValues()
            values.put(ET_PISDELETE, 1)    // 바꾸고자 하는 내용
            db.update(ET_TABLE_NAME, values, "$ET_PTEXT=? and $ET_PDATE=?", arrayOf(data.title, data.date))
            cursor.close()
            db.close()
            return true
        }
        else {
            cursor.close()
            db.close()
            return false
        }
    }

    fun ET_getAllRecord() {
        ETArray.clear()
        ETArray_Del.clear()
        val strsql = "select * from $ET_TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
        if (cursor.count != 0) { // 무언가를 가지고 옴
            ET_getOneRecord(cursor)
        }
        cursor.close()
        db.close()
    }

    private fun ET_getOneRecord(cursor :Cursor) {
        cursor.moveToFirst()
        do {
            if (cursor.getInt(3) == 1)    // true : 지워짐
                ETArray_Del.add(emoTrashData(cursor.getString(1), cursor.getString(2), true))
            else    // 안지워짐
                ETArray.add(emoTrashData(cursor.getString(1), cursor.getString(2), false))
            // 실제 캘린더뷰에 갱신
            Log.i("감쓰 데이터 목록 : ", cursor.getString(1))
        } while(cursor.moveToNext())
    }
}