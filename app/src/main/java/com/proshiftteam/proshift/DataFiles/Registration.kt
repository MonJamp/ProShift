package com.proshiftteam.proshift.DataFiles

data class Registration (
    var username: String,
    var first_name: String,
    var last_name: String,
    var phone: Int,
    var company_code: String,
    var email: String,
    var password: String,
    var re_password: String
)