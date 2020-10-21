package com.proshiftteam.proshift.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_main.*

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
            val username: String = editTextLoginUsername.text.toString()
            val password: String = editTextLoginPassword.text.toString()

            if (username=="mlogin" && password=="password") {

                // Temporary Code for manager login for testing
                val accessCode = 1
                val intentToHome = Intent(this, HomeActivity::class.java)
                intentToHome.putExtra("accessCode", accessCode)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startActivity(intentToHome)


            } else if (username=="elogin" && password=="password") {

                // Temporary Code for employee login for testing
                val accessCode = 0
                val intentToHome = Intent(this, HomeActivity::class.java)
                intentToHome.putExtra("accessCode", accessCode)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startActivity(intentToHome)

            } else {
                Toast.makeText(this, "Incorrect information", Toast.LENGTH_LONG).show()
                // Code for retrieving user information and connecting with appropriate homescreen


            }
        }
    }
}
