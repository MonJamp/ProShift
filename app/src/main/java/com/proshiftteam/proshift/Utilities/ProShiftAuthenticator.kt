/*
Copyright 2020 ProShift Team

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.proshiftteam.proshift.Utilities

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.proshiftteam.proshift.Activities.AccountActivity
import com.proshiftteam.proshift.DataFiles.LoginObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProShiftAuthenticator : AbstractAccountAuthenticator {
    companion object {
        lateinit var mContext: Context
            private set
    }

    constructor(context: Context) : super(context) {
        mContext = context
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        val intent = Intent(mContext, AccountActivity::class.java)
        intent.putExtra("com.proshiftteam.proshift", accountType)
        intent.putExtra("auth_token", authTokenType)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        var am = AccountManager.get(mContext)
        var authToken = am.peekAuthToken(account, authTokenType)

        if(TextUtils.isEmpty(authToken)) {
            val loginValuesObject = LoginObject(am.getPassword(account), account!!.name)
            val callApiPost = RetrofitBuilderObject.connectJsonApiCalls.loginUser(loginValuesObject)

            callApiPost.enqueue(object : Callback<LoginObject> {
                override fun onFailure(call: Call<LoginObject>, t: Throwable) {

                }

                override fun onResponse(call: Call<LoginObject>, response: Response<LoginObject>) {
                    if(response.isSuccessful) {
                        authToken = response.body()?.auth_token
                    } else {

                    }
                }
            })
        }

        if(!TextUtils.isEmpty(authToken)) {
            val bundle = Bundle()
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account?.name)
            bundle.putString("com.proshiftteam.proshift", account?.type)
            bundle.putString("auth_token", authTokenType)
            return bundle
        }

        val intent = Intent(mContext, AccountActivity::class.java)
        intent.putExtra("com.proshiftteam.proshift", account?.type)
        intent.putExtra("auth_token", authTokenType)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    override fun getAuthTokenLabel(authTokenType: String?): String {
        TODO("Not yet implemented")
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle {
        TODO("Not yet implemented")
    }
}