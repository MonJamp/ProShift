package com.proshiftteam.proshift.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import com.proshiftteam.proshift.DataFiles.Registration
import com.proshiftteam.proshift.Interfaces.ApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnSubmit = findViewById<Button>(R.id.btnRegisterSubmit)

        btnSubmit.setOnClickListener {

            val context = this

            val userNameEntered = plainTextRegisterUsername.text.toString()
            val firstNameEntered = plainTextRegisterFirstName.text.toString()
            val lastNameEntered = plainTextRegisterLastName.text.toString()
            val phoneNumberEntered = (editTextRegisterPhone.text.toString()).toInt()
            val companyCodeEntered = editTextRegisterCompanyCode.text.toString()
            val emailAddressEntered = plainTextRegisterEmail.text.toString()
            val passwordEntered = editTextRegisterPassword.text.toString()
            val rePasswordEntered = editTextConfirmPassword.text.toString()

            val newRegistration = Registration(userNameEntered,firstNameEntered,lastNameEntered,phoneNumberEntered,companyCodeEntered,emailAddressEntered,passwordEntered,rePasswordEntered)

            val httpClient = OkHttpClient.Builder()


            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://proshiftonline.com/api/")
                .client(httpClient.build())
                .build()

            val connectJsonApiCalls = retrofitBuilder.create(ApiCalls::class.java)

            val callApiPost = connectJsonApiCalls.registerUser(newRegistration)

            callApiPost.enqueue(object : Callback<Registration> {
                override fun onFailure(call: Call<Registration>, t: Throwable) {
                    Toast.makeText(context, "Failed to register user!", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<Registration>,
                    response: Response<Registration>
                ) {
                    Toast.makeText(context, "Successfully Registered!", Toast.LENGTH_SHORT).show()
                }

            })

        }
    }
}