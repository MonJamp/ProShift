package com.proshiftteam.proshift.Interfaces


import com.proshiftteam.proshift.DataFiles.*
import okhttp3.ResponseBody
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

    @POST("employee/shift_request2")
    fun requestShiftPickUp(@Header("Authorization") token: String?, @Body pickUpShiftObject: PickUpShiftObject): Call<ResponseBody>

    @POST("manager/create_shift")
    fun createShift(@Header("Authorization") token: String?, @Body shiftObject: ShiftObject): Call<ShiftObject>

    @GET("manager/get_employees")
    fun getEmployees(@Header("Authorization") token: String?): Call<List<EmployeeObject>>

    @GET("manager/get_time_off_requests")
    fun getTimeOffRequests(@Header("Authorization") token: String?): Call<List<GetTimeOffRequestsObject>>

    @GET("manager/get_shift_requests")
    fun getShiftRequests(@Header("Authorization") token: String?): Call<List<GetShiftRequestsObject>>

    @POST("manager/approve_shift_request")
    fun approveShiftRequest(@Header("Authorization") token: String?, @Body approveDenyShiftRequestObject: ApproveDenyShiftRequestObject): Call<ResponseBody>

    @POST("manager/approve_time_off")
    fun approveTimeOffRequest(@Header("Authorization") token: String?, @Body approveDenyTimeOffRequestsObject: ApproveDenyTimeOffRequestsObject): Call<ResponseBody>
}