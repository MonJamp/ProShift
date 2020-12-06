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

    // Register a user
    @POST("users/")
    fun registerUser(@Body registration: Registration): Call<Registration>

    // Login a user
    @POST("token/login/")
    fun loginUser(@Body loginObject: LoginObject): Call <LoginObject>

    @Headers(
        "Accepts: application/json",
        "Content-Type: application/json"
    )

    // Logout user
    @POST("token/logout")
    fun logoutUser(@Header("Authorization") token: String?, @Body logoutObject: LogoutObject): Call<LogoutObject>

    // Get a list of assigned shifts
    @GET("employee/get_shifts")
    fun getAssignedShifts(@Header("Authorization") token: String?): Call<List<AssignedShiftsObject>>

    // Drop selected shift
    @POST("employee/toggle_drop_shift")
    fun dropSelectedShift(@Header("Authorization") token: String?, @Body dropShiftObject: DropShiftObject): Call<ShiftObject>

    // Get a list of open shifts
    @GET("employee/get_open_shifts")
    fun getOpenShifts(@Header("Authorization") token: String?): Call<List<OpenShiftsObject>>

    // Request to pick up a shift
    @POST("employee/shift_request2")
    fun requestShiftPickUp(@Header("Authorization") token: String?, @Body pickUpShiftObject: PickUpShiftObject): Call<ResponseBody>

    // Request time off
    @POST("employee/request_time_off")
    fun requestTimeOff(@Header("Authorization") token: String?, @Body requestTimeOffObject: RequestTimeOffObject): Call<RequestTimeOffObject>

    // Create a shift
    @POST("manager/create_shift")
    fun createShift(@Header("Authorization") token: String?, @Body shiftObject: ShiftObject): Call<ShiftObject>

    // Get a list of employees
    @GET("manager/get_employees")
    fun getEmployees(@Header("Authorization") token: String?): Call<List<EmployeeObject>>

    // Get a list of time off requests
    @GET("manager/get_time_off_requests")
    fun getTimeOffRequests(@Header("Authorization") token: String?): Call<List<GetTimeOffRequestsObject>>

    // Get a list of shift requests
    @GET("manager/get_shift_requests")
    fun getShiftRequests(@Header("Authorization") token: String?): Call<List<GetShiftRequestsObject>>

    // Approve shift request
    @POST("manager/approve_shift_request")
    fun approveShiftRequest(@Header("Authorization") token: String?, @Body approveDenyShiftRequestObject: ApproveDenyShiftRequestObject): Call<ResponseBody>

    // Approve time off request
    @POST("manager/approve_time_off")
    fun approveTimeOffRequest(@Header("Authorization") token: String?, @Body approveDenyTimeOffRequestsObject: ApproveDenyTimeOffRequestsObject): Call<ResponseBody>

    // Deny shift request
    @POST("manager/deny_shift_request")
    fun denyShiftRequest(@Header("Authorization") token: String?, @Body approveDenyShiftRequestObject: ApproveDenyShiftRequestObject): Call<ResponseBody>

    // Deny time off request
    @POST("manager/deny_time_off")
    fun denyTimeOffRequest(@Header("Authorization") token: String?, @Body approveDenyTimeOffRequestsObject: ApproveDenyTimeOffRequestsObject): Call<ResponseBody>

    // Get a list of ALL the shifts
    @GET("manager/get_all_shifts")
    fun viewAllShifts(@Header("Authorization") token: String?): Call<List<ViewAllShiftsObject>>

    // Update a shift
    @POST("manager/update_shift")
    fun updateShift(@Header("Authorization") token: String?, @Body updateShiftObject: UpdateShiftObject): Call<UpdateShiftObject>

    // Check if user is a manger
    @GET("manager/test")
    fun testIfManager(@Header("Authorization") token: String?): Call<ResponseBody>

    // Show a list of pending requests
    @GET("employee/get_shift_requests_employee")
    fun showPendingShiftRequests(@Header("Authorization") token: String?): Call<List<PendingShiftRequestsObject>>

    // Show a list of time off requests
    @GET("employee/get_requested_time_off")
    fun showListOffTimeOffRequests(@Header("Authorization") token: String?): Call<List<ListOfTimeOffRequestsObject>>

    // Generate user code
    @POST("manager/generate_code")
    fun generateUserCode(@Header("Authorization") token: String?, @Body generateUserCodeObject: GenerateUserCodeObject): Call<GenerateUserCodeObject>

    // Get user information
    @GET("employee/get_user_info")
    fun getUserInformation(@Header("Authorization") token: String?): Call<UserInfoObject>

    // Redeem company code
    @POST("employee/reedem_code")
    fun redeemUserCode(@Header("Authorization") token: String?, @Body redeemCodeObject: RedeemCodeObject): Call<RedeemCodeObject>

    // Get a list of positions
    @GET("manager/get_positions")
    fun getPositions(@Header("Authorization") token: String?): Call<List<PositionObject>>

    // Get a list of codes
    @GET("manager/get_codes")
    fun getCodes(@Header("Authorization") token: String?): Call<List<CompanyCodeObject>>

    // Get information for a specific shift
    @POST("manager/get_shift")
    fun getShift(@Header("Authorization") token: String?, @Body pickUpShiftObject: PickUpShiftObject): Call<OpenShiftsObject>
}
