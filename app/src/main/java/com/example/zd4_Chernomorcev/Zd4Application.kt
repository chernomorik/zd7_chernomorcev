package com.example.zd4_Chernomorcev

import android.app.Application

class Zd4Application : Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}