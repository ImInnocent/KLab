package com.example.idealmood

import java.util.*

class UtilManager {

    companion object {
        fun localeLanguage (): String {
            return Locale.getDefault().language
        }

        fun currentLocale(): Locale {
            return if (localeLanguage() == "ko") {
                Locale.KOREA
            } else {
                Locale.ENGLISH
            }
        }
    }
}