package com.proshiftteam.proshift.Utilities

import android.app.TimePickerDialog
import android.content.Context
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

// Allows transforming EditText widgets into a TimePickerDialog
fun EditText.asTimePicker(context: Context, format: String) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val timePickerOnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR, hour)
            calendar.set(Calendar.MINUTE, minute)
            val sdf = SimpleDateFormat(format)
            setText(sdf.format(calendar.time))
        }

    setOnClickListener {
        TimePickerDialog(
            context, timePickerOnTimeSetListener, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true
        ).run {
            show()
        }
    }
}