package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.DataFiles.GenerateUserCodeObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_generate_code.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerateCodeActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_generate_code)
        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")
        val accessCode: Int? = bundle?.getInt("accessCode")



        findViewById<ImageView>(R.id.backArrowButtonGenerateCodeActivity).setOnClickListener {
            val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
            intentToManagerControlsActivity.putExtra("accessCode", accessCode)
            startActivity(intentToManagerControlsActivity)
        }

        generateUserCodeButton.setOnClickListener {
            val emailAddressToSend = emailAddressEnteredForCode.text.toString()

            val generateCodeObjectToSend = GenerateUserCodeObject(emailAddressToSend)
            val callApiGenerateCode = connectJsonApiCalls.generateUserCode("Token $tokenCode", generateCodeObjectToSend)
            callApiGenerateCode.enqueue(object: Callback<GenerateUserCodeObject> {
                override fun onFailure(call: Call<GenerateUserCodeObject>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Error generating Code.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<GenerateUserCodeObject>, response: Response<GenerateUserCodeObject>) {
                    if (response.isSuccessful) {
                        val responseGenerateCodeObject = response.body()
                        val codeForUser = responseGenerateCodeObject?.code
                        userCodeRetreived.text = codeForUser.toString()
                        Toast.makeText(context, "Code retrieved. Response Code: ${response.code()}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error getting the code. Response Code: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}