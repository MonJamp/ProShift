package com.proshiftteam.proshift.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.widget.TextView
import com.proshiftteam.proshift.DataFiles.Registration
import com.proshiftteam.proshift.DataFiles.RegistrationResponse
import com.proshiftteam.proshift.Interfaces.ApiCalls
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
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


            val firstNameEntered = plainTextRegisterFirstName.text.toString()
            val lastNameEntered = plainTextRegisterLastName.text.toString()
            val userNameEntered = plainTextRegisterUsername.text.toString()
            val phoneNumberEntered = editTextRegisterPhone.text.toString()
            val companyCodeEntered = editTextRegisterCompanyCode.text.toString()
            val emailAddressEntered = plainTextRegisterEmail.text.toString()
            val passwordEntered = editTextRegisterPassword.text.toString()
            val rePasswordEntered = editTextConfirmPassword.text.toString()

            val newRegistration = Registration(userNameEntered,firstNameEntered,lastNameEntered,phoneNumberEntered,companyCodeEntered,emailAddressEntered,passwordEntered,rePasswordEntered)

            val callApiPost = connectJsonApiCalls.registerUser(newRegistration)

            callApiPost.enqueue(object : Callback<Registration> {
                override fun onFailure(call: Call<Registration>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect!", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(
                    call: Call<Registration>,
                    response: Response<Registration>
                ) {
                    if (response.isSuccessful) {
                    Toast.makeText(context, "Successfully registered user! Response code " + response.code(), Toast.LENGTH_SHORT).show()
                        val intentToMainActivity = Intent(context, MainActivity::class.java)
                        startActivity(intentToMainActivity)
                    }
                    else {
                        Toast.makeText(context, "Failed registration process : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}
