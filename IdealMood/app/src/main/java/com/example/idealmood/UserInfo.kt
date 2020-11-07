package com.example.idealmood

import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.StringBuilder

class UserInfo {
    companion object {
        // userInfo getter/setter
        private val userInfo:JSONObject = makeInitialUserInfo()

        const val NAME = "name"                 // 이름
        const val NAME_PASSED = "name_passed"   // 이름을 입력했는가
        const val DEFAULT_NAME = "디얼이"        // 기본 이름

        // 변수
        var name: String = getString(NAME)

        private fun makeInitialUserInfo():JSONObject {
            var data:String = FileManager.readFile()

            // add initial or essential data
            return if (data.isNotEmpty()) {
                JSONObject(data)
            } else {
                JSONObject()
            }
        }

        fun get(key: String):Any? {
            if (userInfo.has(key)) {
                return userInfo[key]
            }

            return null
        }

        private fun getString(key: String):String {
            if (userInfo.has(key)) {
                return userInfo[key].toString()
            }

            return ""
        }

        private fun getInt(key:String):Int {
            if (userInfo.has(key)) {
                return userInfo[key].toString().toInt()
            }

            return 0
        }

        private fun getJsonArray(key:String):JSONArray {
            var default:JSONArray = JSONArray()

            if (userInfo.has(key)) {
                return userInfo[key] as JSONArray
            }

            if (key == "dayArray")
                default = defaultDayArray()

            return default
        }

        private fun defaultDayArray():JSONArray {
            return JSONArray(arrayListOf<Int>(0,0,0,0,0,0,0))
        }

        fun updateVar(key: String, value: Any) {
            if (key == NAME) {
                name = value.toString()
            }
        }

        fun set(key: String, value:Any) {
            var map: HashMap<String, Any> = HashMap<String, Any>()

            if (key != null) {
                map[key] = value
            }

            set(map)
        }

        fun set(map: HashMap<String, Any>) {
            for ((key, value) in map) {
                userInfo.put(key, value)
                updateVar(key, value)
            }

            FileManager.saveFile(userInfo)
        }

        fun has(key: String):Boolean {
            return userInfo.has(key)
        }
    }
}