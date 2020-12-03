package com.proshiftteam.proshift.Activities

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.widget.Button
import android.widget.Toast
import com.proshiftteam.proshift.DataFiles.UserInfoObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    val CHOOSE_ACCOUNT = 8888
    lateinit var mContext: Context
    lateinit var btnChooseAccount: Button
    lateinit var prefs: SharedPreferences
    lateinit var accountManager: AccountManager

    fun loginAccount(email: String, tokenCode: String) {
        val callApiUserInformation: Call<UserInfoObject> = connectJsonApiCalls.getUserInformation("Token $tokenCode")
        callApiUserInformation.enqueue(object: Callback<UserInfoObject> {
            override fun onFailure(call: Call<UserInfoObject>, t: Throwable) {
                Toast.makeText(mContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<UserInfoObject>, response: Response<UserInfoObject>) {
                if (response.isSuccessful) {
                    val userInfo = response.body()!!

                    if(userInfo.company_name == "None") {
                        val intentToEnterCodeActivity = Intent(mContext, EnterCodeActivity::class.java)
                        intentToEnterCodeActivity.putExtra("tokenCode", tokenCode)
                        startActivity(intentToEnterCodeActivity)
                    }
                    else if(userInfo.is_manager) {
                        val accessCode = 1
                        val intentToHome = Intent(mContext, HomeActivity::class.java)
                        intentToHome.putExtra("accessCode", accessCode)
                        intentToHome.putExtra("tokenCode", tokenCode)
                        startActivity(intentToHome)
                        Toast.makeText(mContext, "Welcome " + email + "! \nToken: " + tokenCode + " \nAccess level: " + accessCode, Toast.LENGTH_SHORT).show()
                    }
                    else if(!userInfo.is_manager) {
                        val accessCode = 0
                        val intentToHome = Intent(mContext, HomeActivity::class.java)
                        intentToHome.putExtra("accessCode", accessCode)
                        intentToHome.putExtra("tokenCode", tokenCode)
                        startActivity(intentToHome)
                        Toast.makeText(mContext, "Welcome " + email + "! \nToken: " + tokenCode + " \nAccess level: " + accessCode, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(mContext, "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(mContext, "Error getting user info: " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CHOOSE_ACCOUNT) {
            if(resultCode == RESULT_OK) {
                val email = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                val am = AccountManager.get(mContext)
                val accounts = am.getAccountsByType("com.proshiftteam.proshift")
                var account: Account? = null
                for(acc: Account in accounts) {
                    if(acc.name == email) {
                        account = acc
                    }
                }

                val tokenCode = am.peekAuthToken(account, "auth_token")

                // Save chosen account to login automatically
                with(prefs.edit()) {
                    putString(getString(R.string.DEFAULT_ACCOUNT), email!!)
                    putBoolean(getString(R.string.IS_LOGGED_OUT), false)
                    apply()
                }

                loginAccount(email!!, tokenCode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        prefs = this.getSharedPreferences(getString(R.string.PREFERENCES_FILE), Context.MODE_PRIVATE)
        accountManager = AccountManager.get(mContext)

        val isLoggedOut = prefs.getBoolean(getString(R.string.IS_LOGGED_OUT), true)
        val defaultAccount = prefs.getString(getString(R.string.DEFAULT_ACCOUNT), null)
        if(defaultAccount != null && !isLoggedOut) {
            val accounts = accountManager.accounts
            for(acc: Account in accounts) {
                if(acc.name == defaultAccount) {
                    val tokenCode = accountManager.peekAuthToken(acc, "auth_token")
                    loginAccount(acc.name, tokenCode)
                    break
                }
            }
        } else {
            btnChooseAccount = findViewById(R.id.mainChooseAccount)
            btnChooseAccount.setOnClickListener {
                val intent = AccountManager.newChooseAccountIntent(
                    null,
                    null,
                    Array<String>(1) { "com.proshiftteam.proshift" },
                    null,
                    null,
                    null,
                    null
                )
                startActivityForResult(intent, CHOOSE_ACCOUNT)
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        finishAndRemoveTask()
        exitProcess(0)
    }
}
