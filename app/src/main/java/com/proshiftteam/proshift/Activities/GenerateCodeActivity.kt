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
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.DataFiles.GenerateUserCodeObject
import com.proshiftteam.proshift.DataFiles.PositionObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_generate_code.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerateCodeActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var positionMap: HashMap<String, Int> = HashMap<String, Int>()
    var positionNames: ArrayList<String> = ArrayList<String>()
    var selectPosition: String = "Employee"


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectPosition = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    // On create function that assigns a layout, performs click actions for buttons.
    // Also responsible for sending and receiving tokenCode throughout the app to process various API requests.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_generate_code)
        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        // Back button to go back to the previous activity
        findViewById<ImageView>(R.id.backArrowButtonGenerateCodeActivity).setOnClickListener {
            val intentToCompanyCodeActivity = Intent(context, CompanyCodeActivity::class.java)
            intentToCompanyCodeActivity.putExtra("tokenCode", tokenCode)
            intentToCompanyCodeActivity.putExtra("accessCode", accessCode)
            startActivity(intentToCompanyCodeActivity)
        }

        val positionSpinner: Spinner = findViewById(R.id.codePositionSpinner)

        // API call to get a list of positions. This allows the user to add more positions in the future.
        val callApiGetPositions: Call<List<PositionObject>> = connectJsonApiCalls.getPositions("Token $tokenCode")
        callApiGetPositions.enqueue(object : Callback<List<PositionObject>> {
            override fun onFailure(call: Call<List<PositionObject>>, t: Throwable) {

            }

            // Adds the list to a spinner view
            override fun onResponse(
                call: Call<List<PositionObject>>,
                response: Response<List<PositionObject>>
            ) {
                if(response.isSuccessful) {
                    val positionList = response.body()

                    for(position in positionList?.asIterable()!!) {
                        positionMap.put(position.name, position.id)
                        positionNames.add(position.name)
                    }

                    val spinnerAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, positionNames)
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    positionSpinner.adapter = spinnerAdapter
                    positionSpinner.onItemSelectedListener = context
                }
            }
        })

        // Button to generate the code
        codeGenerate.setOnClickListener {
            val position: Int = positionMap[selectPosition]!!

            Toast.makeText(context, "User is a " + selectPosition, Toast.LENGTH_SHORT).show()

            val emailAddressToSend = codeEmail.text.toString()

            val generateCodeObjectToSend = GenerateUserCodeObject(position, emailAddressToSend)

            // API call to get the code assigned by entering an email address and selecting a position
            val callApiGenerateCode = connectJsonApiCalls.generateUserCode("Token $tokenCode", generateCodeObjectToSend)
            callApiGenerateCode.enqueue(object: Callback<GenerateUserCodeObject> {
                override fun onFailure(call: Call<GenerateUserCodeObject>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error generating Code.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<GenerateUserCodeObject>, response: Response<GenerateUserCodeObject>) {
                    if (response.isSuccessful) {
                        val responseGenerateCodeObject = response.body()
                        val codeForUser = responseGenerateCodeObject?.code
                        codeCode.setText(codeForUser.toString(), TextView.BufferType.EDITABLE)
                        Toast.makeText(context, "Code retrieved.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error getting the code. Response Code: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}