package com.proshiftteam.proshift.DataFiles

class ListOfTimeOffRequestsObject (
    var id: Int = 0,
    var company: Int = 0,
    var company_name: String = "",
    var employee: Int = 0,
    var employee_name: String = "",
    var is_approved: Boolean,
    var is_denied:Boolean,
    var start_date: String,
    var end_date: String,
    var time_start: String,
    var time_end: String
)