package com.proshiftteam.proshift.Interfaces


import com.proshiftteam.proshift.DataFiles.LoginObject
import com.proshiftteam.proshift.DataFiles.Registration
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiCalls {
    @POST("users/")
    fun registerUser(@Body registration: Registration): Call<Registration>

    @POST("token/login")
    fun loginUser(@Body loginObject: LoginObject): Call <LoginObject>
}