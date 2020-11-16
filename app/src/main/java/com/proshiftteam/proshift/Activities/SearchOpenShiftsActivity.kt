package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Adapters.MyAdapter
import com.proshiftteam.proshift.Adapters.SearchOpenShiftsAdapter
import com.proshiftteam.proshift.Adapters.showShiftsAdapter
import com.proshiftteam.proshift.DataFiles.AssignedShiftsObject
import com.proshiftteam.proshift.DataFiles.HMS
import com.proshiftteam.proshift.DataFiles.OpenShiftsObject
import com.proshiftteam.proshift.DataFiles.ScheduleData
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_search_open_shifts.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchOpenShiftsActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_open_shifts)
        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")

        val callApiGetOpenShifts: Call<List<OpenShiftsObject>> = RetrofitBuilderObject.connectJsonApiCalls.getOpenShifts("Token $tokenCode")
        callApiGetOpenShifts.enqueue(object: Callback<List<OpenShiftsObject>> {
            override fun onFailure(call: Call<List<OpenShiftsObject>>, t: Throwable) {
                Toast.makeText(context, "Cannot connect! Error displaying open shifts!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<OpenShiftsObject>>, response: Response<List<OpenShiftsObject>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Successfully loaded open shifts" + response.code(), Toast.LENGTH_SHORT).show()
                    val listOfOpenShifts = response.body()!!
                    showSearchOpenShiftsRecyclerView.adapter = SearchOpenShiftsAdapter(tokenCode.toString(), listOfOpenShifts)
                } else {
                    Toast.makeText(context, "Failed loading open shifts : Response code " + response.code(), Toast.LENGTH_SHORT).show()
                }
            }

        })
        
        findViewById<ImageView>(R.id.backArrowButtonSearchOpen).setOnClickListener {
            val intentToHomeActivity = Intent(context, HomeActivity::class.java)
            intentToHomeActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToHomeActivity)
        }
    }
}