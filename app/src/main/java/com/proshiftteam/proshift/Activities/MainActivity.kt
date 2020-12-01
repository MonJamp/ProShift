package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.proshiftteam.proshift.DataFiles.LoginObject
import com.proshiftteam.proshift.DataFiles.UserInfoObject
import com.proshiftteam.proshift.Interfaces.ApiCalls
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import com.proshiftteam.proshift.Utilities.onSubmit
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRegister = findViewById<Button>(R.id.btnLoginRegister)
        btnRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLoginLogin.setOnClickListener {
            val emailAddress: String = editTextLoginUsername.text.toString()
            val password: String = editTextLoginPassword.text.toString()

            val context = this

            if (emailAddress=="mlogin" && password=="password") {

                // Temporary Code for manager login for testing
                val accessCode = 1
                val intentToHome = Intent(this, HomeActivity::class.java)
                intentToHome.putExtra("accessCode", accessCode)
                startActivity(intentToHome)


            } else if (emailAddress=="elogin" && password=="password") {

                // Temporary Code for employee login for testing
                val accessCode = 0
                val intentToHome = Intent(this, HomeActivity::class.java)
                intentToHome.putExtra("accessCode", accessCode)
                startActivity(intentToHome)

            } else if (emailAddress=="NeverGonnaGiveYouUp" && password=="NeverGonnaLetYouDown") {
                startActivityForResult(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ")), 1337)
            } else {


                val loginValuesObject = LoginObject(password, emailAddress)

                val callApiPost = connectJsonApiCalls.loginUser(loginValuesObject)

                callApiPost.enqueue(object : Callback<LoginObject> {
                    override fun onFailure(call: Call<LoginObject>, t: Throwable) {
                        Toast.makeText(context, "Cannot connect!", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(
                        call: Call<LoginObject>,
                        response: Response<LoginObject>
                    ) {
                        if (response.code()==200) {
                            val responseLoginObject = response.body()
                            val tokenCode = responseLoginObject?.auth_token

                            val callApiUserInformation: Call<UserInfoObject> = RetrofitBuilderObject.connectJsonApiCalls.getUserInformation("Token $tokenCode")
                            callApiUserInformation.enqueue(object: Callback<UserInfoObject> {
                                override fun onFailure(call: Call<UserInfoObject>, t: Throwable) {
                                    Toast.makeText(context, "Cannot connect! Error getting user information!", Toast.LENGTH_SHORT).show()
                                }

                                override fun onResponse(call: Call<UserInfoObject>, response: Response<UserInfoObject>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Successfully retrieved user info!", Toast.LENGTH_SHORT).show()
                                        val userInformationObject = response.body()
                                        if (userInformationObject!!.company_name == "None") {
                                            val intentToEnterCodeActivity = Intent(context, EnterCodeActivity::class.java)
                                            intentToEnterCodeActivity.putExtra("tokenCode", tokenCode)
                                            startActivity(intentToEnterCodeActivity)
                                        } else {
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
                                                        Toast.makeText(context, "Welcome " + emailAddress + "! \nToken: " + tokenCode + " \nAccess level: " + accessCode, Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        val accessCode = 0
                                                        val intentToHome = Intent(context, HomeActivity::class.java)
                                                        intentToHome.putExtra("accessCode", accessCode)
                                                        intentToHome.putExtra("tokenCode", tokenCode)
                                                        startActivity(intentToHome)
                                                        Toast.makeText(context, "Welcome " + emailAddress + "! \nToken: " + tokenCode + " \nAccess level: " + accessCode, Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            })
                                        }
                                    } else {
                                        Toast.makeText(context, "Error getting user information! Code: " + response.code(), Toast.LENGTH_SHORT).show()
                                    }
                                }

                            })
                        }
                        else {
                            Toast.makeText(context, "Error Logging in! Response Code: " + response.code(), Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }

        val etPassword = findViewById<EditText>(R.id.editTextLoginPassword)
        etPassword.onSubmit { btnLoginLogin.performClick() }
    }

    override fun onBackPressed() {
    }
}
