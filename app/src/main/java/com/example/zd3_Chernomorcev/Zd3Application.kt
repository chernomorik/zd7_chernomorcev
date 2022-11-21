package com.example.zd3_Chernomorcev

import android.app.Application

class Zd3Application : Application() {

    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}