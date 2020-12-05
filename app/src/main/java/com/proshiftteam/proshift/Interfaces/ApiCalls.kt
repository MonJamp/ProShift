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
    fun dropSelectedShift(@Header("Authorization") token: String?, @Body dropShiftObject: DropShiftObject): Call<ShiftObject>

    @GET("employee/get_open_shifts")
    fun getOpenShifts(@Header("Authorization") token: String?): Call<List<OpenShiftsObject>>

    @POST("employee/shift_request2")
    fun requestShiftPickUp(@Header("Authorization") token: String?, @Body pickUpShiftObject: PickUpShiftObject): Call<ResponseBody>

    @POST("employee/request_time_off")
    fun requestTimeOff(@Header("Authorization") token: String?, @Body requestTimeOffObject: RequestTimeOffObject): Call<RequestTimeOffObject>

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

    @POST("manager/deny_shift_request")
    fun denyShiftRequest(@Header("Authorization") token: String?, @Body approveDenyShiftRequestObject: ApproveDenyShiftRequestObject): Call<ResponseBody>

    @POST("manager/deny_time_off")
    fun denyTimeOffRequest(@Header("Authorization") token: String?, @Body approveDenyTimeOffRequestsObject: ApproveDenyTimeOffRequestsObject): Call<ResponseBody>

    @GET("manager/get_all_shifts")
    fun viewAllShifts(@Header("Authorization") token: String?): Call<List<ViewAllShiftsObject>>

    @POST("manager/update_shift")
    fun updateShift(@Header("Authorization") token: String?, @Body updateShiftObject: UpdateShiftObject): Call<UpdateShiftObject>

    @GET("manager/test")
    fun testIfManager(@Header("Authorization") token: String?): Call<ResponseBody>

    @GET("employee/get_shift_requests_employee")
    fun showPendingShiftRequests(@Header("Authorization") token: String?): Call<List<PendingShiftRequestsObject>>

    @GET("employee/get_requested_time_off")
    fun showListOffTimeOffRequests(@Header("Authorization") token: String?): Call<List<ListOfTimeOffRequestsObject>>

    @POST("manager/generate_code")
    fun generateUserCode(@Header("Authorization") token: String?, @Body generateUserCodeObject: GenerateUserCodeObject): Call<GenerateUserCodeObject>

    @GET("employee/get_user_info")
    fun getUserInformation(@Header("Authorization") token: String?): Call<UserInfoObject>

    @POST("employee/reedem_code")
    fun redeemUserCode(@Header("Authorization") token: String?, @Body redeemCodeObject: RedeemCodeObject): Call<RedeemCodeObject>

    @GET("manager/get_positions")
    fun getPositions(@Header("Authorization") token: String?): Call<List<PositionObject>>

    @GET("manager/get_codes")
    fun getCodes(@Header("Authorization") token: String?): Call<List<CompanyCodeObject>>
}
