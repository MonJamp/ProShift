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

package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Adapters.CompanyCodeAdapter
import com.proshiftteam.proshift.DataFiles.CompanyCodeObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompanyCodeActivity : AppCompatActivity() {

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_code)
        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        val companyCodeRecyclerView: RecyclerView = findViewById(R.id.listOfCompanyCodes)

        // API call to display a list of previously assigned company code
        val callAPiGetCompanyCodes: Call<List<CompanyCodeObject>> = connectJsonApiCalls.getCodes("Token $tokenCode")
        callAPiGetCompanyCodes.enqueue(object: Callback<List<CompanyCodeObject>> {
            override fun onFailure(call: Call<List<CompanyCodeObject>>, t: Throwable) {

            }

            // Lists all the assigned company codes
            override fun onResponse(
                call: Call<List<CompanyCodeObject>>,
                response: Response<List<CompanyCodeObject>>
            ) {
                val listOfCompanyCodes = response.body()!!
                companyCodeRecyclerView.adapter = CompanyCodeAdapter(tokenCode.toString(), accessCode!!, listOfCompanyCodes)
            }
        })

        // Back button to go back to the manager controls activity
        findViewById<ImageView>(R.id.backArrowButtonListOfCompanyCodes).setOnClickListener {
            val intentToManagerActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerActivity.putExtra("tokenCode", tokenCode)
            intentToManagerActivity.putExtra("accessCode", accessCode)
            startActivity(intentToManagerActivity)
        }

        // Button to go to generate company code activity
        findViewById<Button>(R.id.companyCodeGenerate).setOnClickListener {
            val intentToGenerateCodeActivity = Intent(context, GenerateCodeActivity::class.java)
            intentToGenerateCodeActivity.putExtra("tokenCode", tokenCode)
            intentToGenerateCodeActivity.putExtra("accessCode", accessCode)
            startActivity(intentToGenerateCodeActivity)
        }
    }
}