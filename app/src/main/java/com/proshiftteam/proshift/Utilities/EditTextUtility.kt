package com.proshiftteam.proshift.Utilities

import android.app.TimePickerDialog
import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
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
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            val sdf = SimpleDateFormat(format)
            setText(sdf.format(calendar.time))
        }

    setOnClickListener {
        TimePickerDialog(
            context, timePickerOnTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
        ).run {
            show()
        }
    }
}

// Allows setting an action when hitting enter
fun EditText.onSubmit(func: () -> Unit) {
    setOnEditorActionListener { _, actionId, event ->
        if(actionId == EditorInfo.IME_ACTION_DONE) {
            func()
        }
        else if(event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
            func()
        }

        true
    }
}