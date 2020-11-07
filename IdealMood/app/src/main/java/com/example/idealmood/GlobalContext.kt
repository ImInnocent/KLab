package com.example.idealmood

import android.content.Context

abstract class GlobalContext {
    companion object {
        private lateinit var context: Context

        fun setContext(con: Context) {
            context = con
        }

        fun getContext():Context {
            return context
        }
    }
}