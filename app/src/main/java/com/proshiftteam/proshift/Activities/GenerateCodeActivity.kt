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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_generate_code)
        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")

        findViewById<ImageView>(R.id.backArrowButtonGenerateCodeActivity).setOnClickListener {
            val intentToCompanyCodeActivity = Intent(context, CompanyCodeActivity::class.java)
            intentToCompanyCodeActivity.putExtra("tokenCode", tokenCode)
            intentToCompanyCodeActivity.putExtra("accessCode", accessCode)
            startActivity(intentToCompanyCodeActivity)
        }

        val positionSpinner: Spinner = findViewById(R.id.codePositionSpinner)

        val callApiGetPositions: Call<List<PositionObject>> = connectJsonApiCalls.getPositions("Token $tokenCode")
        callApiGetPositions.enqueue(object : Callback<List<PositionObject>> {
            override fun onFailure(call: Call<List<PositionObject>>, t: Throwable) {

            }

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

        codeGenerate.setOnClickListener {
            val position: Int = positionMap[selectPosition]!!

            Toast.makeText(context, "User is a " + selectPosition, Toast.LENGTH_SHORT).show()

            val emailAddressToSend = codeEmail.text.toString()

            val generateCodeObjectToSend = GenerateUserCodeObject(position, emailAddressToSend)
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