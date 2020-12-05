/*
Copyright 2020 ProShift Team

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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
            context, timePickerOnTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
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