package com.proshiftteam.proshift.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.R
import java.text.SimpleDateFormat
import java.util.ArrayList
import android.view.View
import android.widget.*
import com.proshiftteam.proshift.DataFiles.EmployeeObject
import com.proshiftteam.proshift.DataFiles.ShiftObject
import com.proshiftteam.proshift.DataFiles.UpdateShiftObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject
import com.proshiftteam.proshift.Utilities.asTimePicker
import kotlinx.android.synthetic.main.activity_modify_shift.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class ModifyShiftActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val dateFormat: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd")
    val timeFormat: String = "HH:mm"

    var employeeMap: HashMap<String, Int> = HashMap<String, Int>()
    var employeeNames: ArrayList<String> = ArrayList<String>()
    var selectEmployee: String = "None"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_shift)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")

        val date: String? = bundle?.getString("date")
        val startTime: String? = bundle?.getString("start_time")
        val endTime: String? = bundle?.getString("end_time")
        val employeeId: Int? = bundle?.getInt("employeeId")
        val position: String? = bundle?.getString("position")
        val openStatus: Boolean? = bundle?.getBoolean("is_open")
        val id = bundle?.getInt("id")

        val calView: CalendarView = findViewById<CalendarView>(R.id.calrenderViewSelectDateInModifyShift)
        val shift_begin: EditText = findViewById<EditText>(R.id.shiftBeginTimeInModifyShift)
        val shift_end: EditText = findViewById<EditText>(R.id.shiftEndTimeInModifyShift)
        val employeeSpinner: Spinner = findViewById(R.id.selectEmployeeSpinnerInUpdateShift)
        val btnUpdateShift: Button = findViewById<Button>(R.id.updateShiftButtonInModifyShift)


        var cal = Calendar.getInstance()
        calView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            calView.date = cal.timeInMillis
        }

        // Transform EditText to TimePicker
        shift_begin.asTimePicker(context, timeFormat)
        shift_end.asTimePicker(context, timeFormat)

        val callApiGetEmployees: Call<List<EmployeeObject>> = RetrofitBuilderObject.connectJsonApiCalls.getEmployees("Token $tokenCode")
        callApiGetEmployees.enqueue(object: Callback<List<EmployeeObject>> {
            override fun onFailure(call: Call<List<EmployeeObject>>, t: Throwable) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<EmployeeObject>>, response: Response<List<EmployeeObject>>) {
                if(response.code() == 200) {
                    Toast.makeText(context, "Success: " + response.code(), Toast.LENGTH_SHORT).show()
                    val employeeList = response.body()

                    // Get employee names and use a hashmap to keep track of employee ids
                    for(employee in employeeList?.asIterable()!!) {
                        employeeMap.put(employee.employee_name, employee.id)
                        employeeNames.add(employee.employee_name)
                    }

                    val spinnerAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, employeeNames)
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    employeeSpinner.adapter = spinnerAdapter
                    employeeSpinner.onItemSelectedListener = context

                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })
        updateShiftButtonInModifyShift.setOnClickListener{
            val employee: Int = employeeMap[selectEmployee]!!
            val is_open: Boolean = false
            val date: String = dateFormat.format(Date(calView.date))
            val time_start: String = shift_begin.text.toString()
            val time_end: String = shift_end.text.toString()

            val updateShiftObject = UpdateShiftObject(id!!, employee, is_open, date, time_start, time_end)

            val callApiUpdateShift: Call<UpdateShiftObject> = RetrofitBuilderObject.connectJsonApiCalls.updateShift("Token $tokenCode", updateShiftObject)
            callApiUpdateShift.enqueue(object: Callback<UpdateShiftObject> {
                override fun onFailure(call: Call<UpdateShiftObject>, t: Throwable) {
                    Toast.makeText(context, "Cannot connect! Failed to update shift!", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<UpdateShiftObject>, response: Response<UpdateShiftObject>) {
                    if(response.isSuccessful)
                    {
                        Toast.makeText(context, "Success updating shift. Response code: " + response.code(), Toast.LENGTH_SHORT).show()
                        val intentToCurrentShiftAddRemoveActivity = Intent(context, CurrentShiftAddRemoveActivity::class.java)
                        intentToCurrentShiftAddRemoveActivity.putExtra("tokenCode", tokenCode)
                        startActivity(intentToCurrentShiftAddRemoveActivity)
                    }
                    else
                    {
                        Toast.makeText(context, "Error updating shift. Response code " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        findViewById<ImageView>(R.id.backArrowButtonModifyShifts).setOnClickListener {
            val intentToCurrentShiftAddRemoveActivity = Intent(context, CurrentShiftAddRemoveActivity::class.java)
            intentToCurrentShiftAddRemoveActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToCurrentShiftAddRemoveActivity)
        }

    }
    fun append(arr: Array<String>, element: String): Array<String> {
        var list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectEmployee = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}