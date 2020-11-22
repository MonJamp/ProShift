package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Adapters.MyAdapter
import com.proshiftteam.proshift.Adapters.ViewAllShiftsAdapter
import com.proshiftteam.proshift.DataFiles.HMS
import com.proshiftteam.proshift.DataFiles.ScheduleData
import com.proshiftteam.proshift.DataFiles.ViewAllShiftsObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_current_shift_add_remove.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class CurrentShiftAddRemoveActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_shift_add_remove)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")

        val callApiViewAllShifts: Call<List<ViewAllShiftsObject>> = RetrofitBuilderObject.connectJsonApiCalls.viewAllShifts("Token $tokenCode")
        callApiViewAllShifts.enqueue(object: Callback<List<ViewAllShiftsObject>> {
            override fun onFailure(call: Call<List<ViewAllShiftsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying all the shifts!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<List<ViewAllShiftsObject>>,
                response: Response<List<ViewAllShiftsObject>>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Successfully displaying all the shifts " + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfEveryShift = response.body()!!
                    recycler_view_View_all_shifts.adapter = ViewAllShiftsAdapter(tokenCode.toString(), listOfEveryShift)
                } else {
                    Toast.makeText(context, "Failed loading open shifts : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

        })

        findViewById<ImageView>(R.id.backArrowButtonAddRemove).setOnClickListener {
            val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToManagerControlsActivity)
        }

    }
}