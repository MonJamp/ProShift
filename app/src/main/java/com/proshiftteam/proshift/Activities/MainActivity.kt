package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.proshiftteam.proshift.DataFiles.LoginObject
import com.proshiftteam.proshift.Interfaces.ApiCalls
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_main.*
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
                            val accessCode = 1
                            val tokenCode = responseLoginObject?.auth_token
                            val intentToHome = Intent(context, HomeActivity::class.java)
                            intentToHome.putExtra("accessCode", accessCode)
                            intentToHome.putExtra("tokenCode", tokenCode)
                            startActivity(intentToHome)

                            Toast.makeText(context, "Welcome " + emailAddress + "! Authorization Token: " + tokenCode, Toast.LENGTH_SHORT).show() }
                        else {
                            Toast.makeText(context, "Error Logging in! Response Code: " + response.code(), Toast.LENGTH_SHORT).show()
                        }
                    }
                })

                // Code for retrieving user information and connecting with appropriate homescreen


            }
        }
    }

    override fun onBackPressed() {
    }
}
