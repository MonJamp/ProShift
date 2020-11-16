package com.proshiftteam.proshift.Interfaces


import com.proshiftteam.proshift.DataFiles.*
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

    @POST("employee/toggle_drop_shift")
    fun dropSelectedShift(@Header("Authorization") token: String?, @Body dropShiftObject: DropShiftObject): Call<DropShiftObject>

    @GET("employee/get_open_shifts")
    fun getOpenShifts(@Header("Authorization") token: String?): Call<List<OpenShiftsObject>>

    @POST("employee/shift_request")
    fun requestShiftPickUp(@Header("Authorization") token: String?, @Body pickUpShiftObject: PickUpShiftObject): Call<PickUpShiftObject>
}