package com.proshiftteam.proshift.Interfaces


import com.proshiftteam.proshift.DataFiles.AssignedShiftsObject
import com.proshiftteam.proshift.DataFiles.LoginObject
import com.proshiftteam.proshift.DataFiles.LogoutObject
import com.proshiftteam.proshift.DataFiles.Registration
import retrofit2.Call
import retrofit2.http.*

interface ApiCalls {

    @POST("users/")
    fun registerUser(@Body registration: Registration): Call<Registration>

    @POST("token/login/")
    fun loginUser(@Body loginObject: LoginObject): Call <LoginObject>

    @Headers(
        "Accepts: application/json",
        "Content-Type: application/json"
    )
    @POST("token/logout")
    fun logoutUser(@Header("Authorization") token: String?, @Body logoutObject: LogoutObject): Call<LogoutObject>

    @GET("employee/get_shifts")
    fun getAssignedShifts(@Header("Authorization") token: String?): Call<List<AssignedShiftsObject>>
}