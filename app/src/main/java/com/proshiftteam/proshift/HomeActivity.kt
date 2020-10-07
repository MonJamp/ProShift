package com.proshiftteam.proshift

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewManager = LinearLayoutManager(this)

        // Example data
        val dataset: Array<ScheduleData> = arrayOf(
            ScheduleData("September", 21, HMS(9, 0), HMS(5, 0)),
            ScheduleData("November", 22, HMS(10, 0), HMS(5, 0)),
            ScheduleData("October", 25, HMS(12, 0), HMS(5, 0))
        )

        viewAdapter = MyAdapter(dataset)

        recyclerView = findViewById<RecyclerView>(R.id.rvScheduleList).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }
}