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

package com.proshiftteam.proshift.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.DataFiles.RedeemCodeObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_enter_code.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EnterCodeActivity:AppCompatActivity() {
    lateinit var prefs: SharedPreferences

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = this.getSharedPreferences(getString(R.string.PREFERENCES_FILE), Context.MODE_PRIVATE)

        setContentView(R.layout.activity_enter_code)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")

        // Back arrow button to go back to the previous activity (Main/Login)
        backArrowButtonEnterCodeScreen.setOnClickListener{
            with(prefs.edit()) {
                putBoolean(getString(R.string.IS_LOGGED_OUT), true)
                apply()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Button to submit the code given to the user
        oneTimeCompanyCodeButton.setOnClickListener {
            val codeEntered: Int = (numberEnteredForCompanyCode.text).toString().toInt()
            val codeSendObject = RedeemCodeObject(codeEntered)
            val callApiRedeemCode = connectJsonApiCalls.redeemUserCode("Token $tokenCode", codeSendObject)
            callApiRedeemCode.enqueue(object: Callback<RedeemCodeObject> {
                override fun onFailure(call: Call<RedeemCodeObject>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error redeeming code.", Toast.LENGTH_SHORT).show()
                }

                // Sends the code to the server to verify user
                override fun onResponse(call: Call<RedeemCodeObject>, response: Response<RedeemCodeObject>) {
                    if (response.code() == 202) {

                        // API call to test if the user is a manager
                        val callApiCheckIfManager: Call<ResponseBody> = RetrofitBuilderObject.connectJsonApiCalls.testIfManager("Token $tokenCode")
                        callApiCheckIfManager.enqueue(object: Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(context, "Cannot connect! Error logging in, please try again!", Toast.LENGTH_SHORT).show()
                            }

                            // Determines the value of accessCode based on what the server returns, 1 is for managers and 0 for employees
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                if (response.isSuccessful) {
                                    val accessCode = 1
                                    val intentToHome = Intent(context, HomeActivity::class.java)
                                    intentToHome.putExtra("accessCode", accessCode)
                                    intentToHome.putExtra("tokenCode", tokenCode)
                                    startActivity(intentToHome)
                                    Toast.makeText(context, "Welcome " + "! \nToken: " + tokenCode + " \nAccess level: " + accessCode, Toast.LENGTH_SHORT).show()
                                } else {
                                    val accessCode = 0
                                    val intentToHome = Intent(context, HomeActivity::class.java)
                                    intentToHome.putExtra("accessCode", accessCode)
                                    intentToHome.putExtra("tokenCode", tokenCode)
                                    startActivity(intentToHome)
                                    Toast.makeText(context, "Welcome " + "! \nToken: " + tokenCode + " \nAccess level: " + accessCode, Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    } else {
                        Toast.makeText(context, "Incorrect user code. Please try again! Response Code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }

            })
        }
    }

    // Back button
    override fun onBackPressed() {
        super.onBackPressed()
        with(prefs.edit()) {
            putBoolean(getString(R.string.IS_LOGGED_OUT), true)
            apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}