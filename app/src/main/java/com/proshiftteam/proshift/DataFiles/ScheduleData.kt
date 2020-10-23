package com.proshiftteam.proshift.DataFiles

/**
 * Hour Minute Second
 */
class HMS(val hour: Int, val minute: Int, val second: Int = 0) {

}

class ScheduleData(val month: String, val date: Int, val startTime: HMS, val endTime: HMS) {

}