package com.example.idealmood

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.*
import java.lang.StringBuilder

class FileManager {
    companion object {
        const val USER_INFO: String = "userInfo"

        // for util
        private val c: Context = GlobalContext.getContext()

        // 파일 읽기
        private fun getPath(path: String): File? {
            var file: File? = c.getFileStreamPath(path)

            if (file == null || !file.exists()) {
                return null
            }

            return file
        }

        fun reset() {
            saveFile(JSONObject())
        }

        fun saveFile(data: Any, fileName: String = USER_INFO) {
            val os = c.openFileOutput(fileName, AppCompatActivity.MODE_PRIVATE)
            val bw = BufferedWriter(OutputStreamWriter(os))
            bw.write(data.toString())
            bw.flush()
        }

        fun readFile(fileName: String = USER_INFO): String {
            var os: FileInputStream
            try {
                os = c.openFileInput(fileName)

            } catch (ex: FileNotFoundException) {
                saveFile(JSONObject())

                return ""
            }

            val br = BufferedReader(InputStreamReader(os))
            var strBuilder = StringBuilder()
            var line = br.readLine()
            while (line != null) {
                // 읽어서 한줄씩 추가
                strBuilder.append(line).append("\n")
                line = br.readLine()
            }
            br.close()

            return strBuilder.toString()
        }
    }
}