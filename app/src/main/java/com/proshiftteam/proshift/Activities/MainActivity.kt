package com.proshiftteam.proshift.Activities

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.proshiftteam.proshift.DataFiles.UserInfoObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    val CHOOSE_ACCOUNT = 8888
    lateinit var mContext: Context

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
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this

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
