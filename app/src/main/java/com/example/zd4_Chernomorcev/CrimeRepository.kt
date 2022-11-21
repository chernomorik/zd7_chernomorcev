package com.example.zd4_Chernomorcev

import android.arch.lifecycle.LiveData
import android.content.Context
import androidx.room.Room
import com.example.zd3_Chernomorcev.Crime
import database.CrimeDatabase
import java.util.*

private const val DATABASE_NAME = "crimedatabase"

class CrimeRepository private constructor(context: Context){
    private val database : CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val crimeDao = database.crimeDao()
    fun getCrimes() : LiveData<List<com.example.zd3_Chernomorcev.Crime>> = crimeDao.getCrimes()

    fun getCrime(id: UUID) : LiveData<Crime?> = crimeDao.getCrime(id)

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if(INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get() : CrimeRepository {
            return INSTANCE ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}