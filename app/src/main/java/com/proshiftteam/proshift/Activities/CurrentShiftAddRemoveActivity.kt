package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proshiftteam.proshift.Adapters.MyAdapter
import com.proshiftteam.proshift.DataFiles.HMS
import com.proshiftteam.proshift.DataFiles.ScheduleData
import com.proshiftteam.proshift.R

class CurrentShiftAddRemoveActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_current_shift_add_remove)
        findViewById<ImageView>(R.id.backArrowButtonAddRemove).setOnClickListener {
            startActivity(Intent(this, ManagerControlsActivity::class.java))
        }
        super.onCreate(savedInstanceState)

        viewManager = LinearLayoutManager(this)
        val exampleData: Array<ScheduleData> = arrayOf(
            ScheduleData(
                "September",
                21,
                HMS(9, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "November",
                22,
                HMS(10, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "October",
                25,
                HMS(12, 0),
                HMS(5, 0)
            ),

            // Added more values for testing purposes
            ScheduleData(
                "September",
                21,
                HMS(9, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "November",
                22,
                HMS(10, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "October",
                25,
                HMS(12, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "September",
                21,
                HMS(9, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "November",
                22,
                HMS(10, 0),
                HMS(5, 0)
            ),
            ScheduleData(
                "October",
                25,
                HMS(12, 0),
                HMS(5, 0)
            )
        )
        viewAdapter = MyAdapter(exampleData)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewSampleList).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }
}