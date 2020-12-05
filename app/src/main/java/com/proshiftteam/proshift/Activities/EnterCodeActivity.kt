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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = this.getSharedPreferences(getString(R.string.PREFERENCES_FILE), Context.MODE_PRIVATE)

        setContentView(R.layout.activity_enter_code)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")

        backArrowButtonEnterCodeScreen.setOnClickListener{
            with(prefs.edit()) {
                putBoolean(getString(R.string.IS_LOGGED_OUT), true)
                apply()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        oneTimeCompanyCodeButton.setOnClickListener {
            val codeEntered: Int = (numberEnteredForCompanyCode.text).toString().toInt()
            val codeSendObject = RedeemCodeObject(codeEntered)
            val callApiRedeemCode = connectJsonApiCalls.redeemUserCode("Token $tokenCode", codeSendObject)
            callApiRedeemCode.enqueue(object: Callback<RedeemCodeObject> {
                override fun onFailure(call: Call<RedeemCodeObject>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error redeeming code.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<RedeemCodeObject>, response: Response<RedeemCodeObject>) {
                    if (response.code() == 202) {
                        val callApiCheckIfManager: Call<ResponseBody> = RetrofitBuilderObject.connectJsonApiCalls.testIfManager("Token $tokenCode")
                        callApiCheckIfManager.enqueue(object: Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(context, "Cannot connect! Error logging in, please try again!", Toast.LENGTH_SHORT).show()
                            }

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