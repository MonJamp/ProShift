package com.proshiftteam.proshift.Activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.proshiftteam.proshift.DataFiles.EmployeeObject
import com.proshiftteam.proshift.DataFiles.ShiftObject
import com.proshiftteam.proshift.Interfaces.RetrofitBuilderObject.connectJsonApiCalls
import com.proshiftteam.proshift.R
import com.proshiftteam.proshift.Utilities.asTimePicker
import kotlinx.android.synthetic.main.activity_create_new_shift.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CreateNewShiftActivity: AppCompatActivity(), AdapterView.OnItemSelectedListener {

    // Temporary values for the spinner. Will be replaced by values retrieved from the GET request later
    var listOfEmployees = arrayOf("Rupin", "Filjo", "Muhammad", "Richard")
    lateinit var spinnerTextView: TextView

    fun Spinner.setItem(list: ArrayList<String>, value: String) {
        val index = list.indexOf(value)
        this.post { this.setSelection(index)}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_new_shift)

        val context = this
        val bundle: Bundle? = intent.extras
        val tokenCode: String? = bundle?.getString("tokenCode")

        findViewById<ImageView>(R.id.backArrowButtonCreateNewShift).setOnClickListener {
            val intentToManagerControlsActivity = Intent(context, ManagerControlsActivity::class.java)
            intentToManagerControlsActivity.putExtra("tokenCode", tokenCode)
            startActivity(intentToManagerControlsActivity)
        }

        // Add date functions
        var dateFormat = SimpleDateFormat("YYYY-MM-dd")
        val timeFormat = "HH:mm"

        var cal = Calendar.getInstance()
        val calView = findViewById<CalendarView>(R.id.calrenderViewSelectDate)
        calView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            calView.date = cal.timeInMillis
        }

        val shift_begin = findViewById<EditText>(R.id.shiftBeginTime)
        shift_begin.asTimePicker(context, timeFormat)
        val shift_end = findViewById<EditText>(R.id.shiftEndTime)
        shift_end.asTimePicker(context, timeFormat)

        // Get list of employees
        var employeeMap = HashMap<String, Int>()
        var employeeNames: Array<String> = arrayOf("None")
        val callApiGetEmployees: Call<List<EmployeeObject>> = connectJsonApiCalls.getEmployees("Token $tokenCode")
        callApiGetEmployees.enqueue(object: Callback<List<EmployeeObject>> {
            override fun onFailure(call: Call<List<EmployeeObject>>, t: Throwable) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<List<EmployeeObject>>,
                response: Response<List<EmployeeObject>>
            ) {
                if(response.code() == 200) {
                    Toast.makeText(context, "Success: " + response.code(), Toast.LENGTH_SHORT).show()
                    val employeeList = response.body()
                    for(employee in employeeList?.asIterable()!!) {
                        employeeMap.put(employee.employee_name, employee.id)
                        employeeNames = append(employeeNames, employee.employee_name)
                    }

                    spinnerTextView = findViewById(R.id.createShiftSpinnerTextView)
                    val employeeSpinner: Spinner = findViewById(R.id.selectEmployeeSpinnerInNewShift)
                    val spinnerAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, employeeNames.asList())
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    employeeSpinner.adapter = spinnerAdapter
                    employeeSpinner.onItemSelectedListener = context

                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        })

        val btnCreateShift = findViewById<Button>(R.id.createNewShiftButtonInCreateNewShift)
        btnCreateShift.setOnClickListener{
            val company: Int = 1
            val employee: Int = employeeMap["Bill Gates"]!!
            val is_open: Boolean = false
            val is_dropped: Boolean = false
            val date: String = dateFormat.format(Date(calView.date))
            val time_start: String = shift_begin.text.toString()
            val time_end: String = shift_end.text.toString()

            val shiftObject = ShiftObject(company, employee, is_open, is_dropped, date, time_start, time_end)

            val context = this
            val bundle: Bundle? = intent.extras
            val tokenCode: String? = bundle?.getString("tokenCode")

            val callApiCreateShift: Call<ShiftObject> = connectJsonApiCalls.createShift("Token $tokenCode", shiftObject)
            callApiCreateShift.enqueue(object: Callback<ShiftObject> {
                override fun onFailure(call: Call<ShiftObject>, t: Throwable) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ShiftObject>, response: Response<ShiftObject>) {
                    if(response.code() == 201)
                    {
                        Toast.makeText(context, "Success: " + response.code(), Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    fun append(arr: Array<String>, element: String): Array<String> {
        var list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val value: String = parent?.getItemAtPosition(position).toString()
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
