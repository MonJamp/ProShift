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

package com.proshiftteam.proshift.DataFiles

class ViewAllShiftsObject (
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