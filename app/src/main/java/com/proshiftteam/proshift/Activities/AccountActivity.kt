package com.proshiftteam.proshift.Activities

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.DataFiles.LoginObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import com.proshiftteam.proshift.Utilities.onSubmit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountActivity : AppCompatActivity() {
    val REQUEST_SIGNUP = 7777

    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var tvSignup: TextView
    lateinit var mContext: Context

    fun loginClick() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        login(email, password)
    }

    fun login(email: String, password: String) {
        val loginObject = LoginObject(password, email)
        val loginApi = connectJsonApiCalls.loginUser(loginObject)
        loginApi.enqueue(object : Callback<LoginObject> {
            override fun onFailure(call: Call<LoginObject>, t: Throwable) {
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<LoginObject>, response: Response<LoginObject>) {
                if(response.isSuccessful) {
                    // Add account to account manager
                    var account = Account(email, "com.proshiftteam.proshift")
                    var am = AccountManager.get(mContext)
                    am.addAccountExplicitly(account, password, null)
                    am.setAuthToken(account, "auth_token", response.body()!!.auth_token)
                    // Let account manager know an account was added
                    val intent = Intent()
                    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, email)
                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, "com.proshiftteam.proshift")
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(mContext, response.errorBody().toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun signup() {
        val intent = Intent(mContext, SignupActivity::class.java)
        startActivityForResult(intent, REQUEST_SIGNUP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_SIGNUP) {
            if(resultCode == RESULT_OK) {
                val email = data!!.getStringExtra("email")
                val password = data!!.getStringExtra("password")
                login(email!!, password!!)
            }
            else if(resultCode == RESULT_CANCELED) {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        mContext = this

        etEmail = findViewById<EditText>(R.id.accountEmail)
        etPassword = findViewById<EditText>(R.id.accountPassword)
        btnLogin = findViewById<Button>(R.id.accountLogin)
        tvSignup = findViewById<TextView>(R.id.accountSignup)

        btnLogin.setOnClickListener { loginClick() }
        tvSignup.setOnClickListener { signup() }
        etPassword.onSubmit { btnLogin.performClick() }
    }
}