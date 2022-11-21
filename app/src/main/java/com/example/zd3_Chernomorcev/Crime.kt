package com.example.zd3_Chernomorcev

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crime(@PrimaryKey val mId: UUID = UUID.randomUUID(),
                 public var mTitle: String? = "",
                 var mDate: Date = Date(),
                 var mSolved: Boolean = false,
                 var mSuspect: String = "") {



    private var mRequestPolice= false

    fun RequiresPolice(): Boolean {
        return mRequestPolice
    }


    fun setRequestPolice(mReqPolice: Boolean) {
        mRequestPolice = mReqPolice
    }

    fun getId() : UUID?
    {
        return mId
    }

    fun getTitle(): String? {
        return mTitle
    }

    fun setTitle(mTitle: String?) {
        this.mTitle = mTitle
    }

    fun getDate(): Date? {
        return mDate
    }


    fun isSolved(): Boolean? {
        return mSolved
    }

    fun setSolved(_mSolved: Boolean){
        mSolved = _mSolved
    }

    fun getTime(): Long{
        return mDate!!.time
    }

    fun setTime(mTime: Long) {
        mDate!!.time = mTime
    }

    fun getSuspect(): String? {
        return mSuspect
    }

    fun setSuspect(suspect: String) {
        mSuspect = suspect
    }

    fun getPhotoFilename(): String? {
        return "IMG" + getId().toString() + ".jpg"
    }
}
