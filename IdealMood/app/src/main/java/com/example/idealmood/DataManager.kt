package com.example.idealmood

class DataManager private constructor() {
    var isStarted: Boolean = false
    var heartBeat: Int = 100

    companion object {
        @Volatile private var instance: DataManager? = null

        @JvmStatic fun getInstance(): DataManager =
            instance ?: synchronized(this) {
                instance ?: DataManager().also {
                    instance = it
                }
            }
    }
}