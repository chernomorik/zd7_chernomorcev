package com.example.zd3_Chernomorcev

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Build

import android.os.Bundle
import android.support.annotation.RequiresApi
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.sql.Time
import java.util.*

private const val  ARG_TIME ="time"
class TimePickerFragment : DialogFragment() {
    interface Callbacks{
        fun onTimeSelected(time: Date)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener= TimePickerDialog.OnTimeSetListener { _: TimePicker, hour: Int, minute: Int ->
            val resultTime : Date =
                GregorianCalendar(0, 0, 0,hour,minute).time
            targetFragment?.let { fragment ->
                (fragment as
                        Callbacks).onTimeSelected(resultTime)}
        }
        val time=arguments?.getSerializable(ARG_TIME) as Date
        val calendar=Calendar.getInstance()
        calendar.time=time
        var initialHour=calendar.get(Calendar.HOUR)
        var initialMinute=calendar.get(Calendar.MINUTE)
        return TimePickerDialog(requireContext(),timeListener,initialHour,initialMinute, true)
    }
    companion object{
        fun newInstance(time: Date?): TimePickerFragment {
            var args=Bundle().apply {
                putSerializable(ARG_TIME,time)
            }
            return TimePickerFragment().apply {
                arguments=args
            }
        }
    }
}