package com.example.idealmood

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyDBHelper(val context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        val DB_VERSION = 1
        val DB_NAME = "idealmood_record.db"

        // 심박수 테이블
        val HB_TABLE_NAME = "heartbeat"
        val HB_PID = "pid"
        val HB_PHEARTBEAT = "pheartbeat"
        val HB_PDATE = "pdate"

        //캘린더 테이블

        // 감쓰 테이블
    }

    val HBArray = ArrayList<Int>()

    override fun onCreate(db: SQLiteDatabase?) {
        // 심박수 테이블 생성
        val create_table = "create table if not exists $HB_TABLE_NAME (" +
                "$HB_PID integer primary key autoincrement, " +
                "$HB_PHEARTBEAT integer, " +
                "$HB_PDATE text)"   // autoincrement : 자동 증가
        db?.execSQL(create_table)    // db 실행, select 구문 제외 insert, delete 등등 실행 가능

        // 캘린더 테이블 생성

        // 감쓰 테이블 생성
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 버전 정보가 바뀌었을 때
        val drop_table = "drop table if exists $HB_TABLE_NAME"
        db?.execSQL(drop_table) // dp 드랍하고
        onCreate(db)    // 다시 만들기
    }


    /////// 심박수 테이블 관련 함수 ///////
    fun HB_insertData(heartbeat :Int) :Boolean { // INSERT, 삽입 성공 여부
        val values = ContentValues()
        values.put(HB_PHEARTBEAT, heartbeat)
        val db=this.writableDatabase    // DB table 객체 획득
        if(db.insert(HB_TABLE_NAME, null, values) > 0) { // insert가 제대로 안 되었을 경우 -1 반환
            db.close()
            return true
        }
        else {
            db.close()
            return false
        }
    }

    // 쓸 일이 있을까? 싶지만 만들어보는 심박수 데이터 삭제 함수 (날짜 기준)
    fun HB_deleteData(pdate :String) :Boolean {
        val strsql = "select * from $HB_TABLE_NAME where " +
                "$HB_PDATE = \'$pdate\'"
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
}