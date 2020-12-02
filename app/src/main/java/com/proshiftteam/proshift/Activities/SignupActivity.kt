package com.proshiftteam.proshift.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.DataFiles.Registration
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import com.proshiftteam.proshift.Utilities.onSubmit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    val REQUEST_SIGNUP = 7777

    lateinit var etFirstName: EditText
    lateinit var etLastName: EditText
    lateinit var etUsername: EditText
    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var etPhone: EditText
    lateinit var btnSubmit: Button
    lateinit var tvLogin: TextView
    lateinit var mContext: Context

    fun signup() {
        val firstName = etFirstName.text.toString()
        val lastName = etLastName.text.toString()
        val username = etUsername.text.toString()
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()
        val phone = etPhone.text.toString()

        val registration = Registration(username, firstName, lastName, phone, email, password, confirmPassword)
        val registrationApi = connectJsonApiCalls.registerUser(registration)

        registrationApi.enqueue(object : Callback<Registration> {
            override fun onFailure(call: Call<Registration>, t: Throwable) {
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Registration>, response: Response<Registration>) {
                if(response.isSuccessful) {
                    val intent = Intent()
                    intent.putExtra("email", email)
                    intent.putExtra("password", password)
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(mContext, response.errorBody().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun login() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        mContext = this

        etFirstName = findViewById(R.id.signupFirstName)
        etLastName = findViewById(R.id.signupLastName)
        etUsername = findViewById(R.id.signupUsername)
        etEmail = findViewById(R.id.signupEmail)
        etPassword = findViewById(R.id.signupPassword)
        etConfirmPassword = findViewById(R.id.signupConfirmPassword)
        etPhone = findViewById(R.id.signupPhone)
        btnSubmit = findViewById(R.id.signupSubmit)
        tvLogin = findViewById(R.id.signupLogin)

        btnSubmit.setOnClickListener { signup() }
        tvLogin.setOnClickListener { login() }
        etPhone.onSubmit { btnSubmit.performClick() }
    }
}