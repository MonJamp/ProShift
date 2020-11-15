package com.proshiftteam.proshift.DataFiles

data class AssignedShiftsObject (
    var id: Int = 0,
    var company: Int = 0,
    var company_name: String = "",
    var employee: Int = 0,
    var employee_name: String = "",
    var position: String = "",
    var is_open: Boolean,
    var is_dropped: Boolean,
    var date: String,
    var time_start: String,
    var time_end: String
)