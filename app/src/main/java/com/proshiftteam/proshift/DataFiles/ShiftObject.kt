package com.proshiftteam.proshift.DataFiles

data class ShiftObject (
    var employee: Int?,
    var is_open: Boolean,
    var is_dropped: Boolean,
    var date: String,
    var time_start: String,
    var time_end: String
)