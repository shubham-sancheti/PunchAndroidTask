package com.example.todolistinkotlin

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistinkotlin.Analytics.logEvent
import com.example.todolistinkotlin.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.alert
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnItemClick {

    val list = mutableListOf<ToDoListData>()

    val c = Calendar.getInstance()

    val month: Int = c.get(Calendar.MONTH)
    val year: Int = c.get(Calendar.YEAR)
    val day: Int = c.get(Calendar.DAY_OF_MONTH)

    var cal = Calendar.getInstance()

    private val listAdapter = ListAdapter(list, this)

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: ToDoListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logEvent(FirebaseAnalytics.Event.APP_OPEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModel = ViewModelProviders.of(this).get(ToDoListViewModel::class.java)

        binding.rvTodoList.layoutManager = LinearLayoutManager(this)
        binding.rvTodoList.adapter = listAdapter
        binding.vieModel = viewModel


        viewModel.getPreviousList()

        viewModel.toDoList.observe(this, androidx.lifecycle.Observer {
            //list.addAll(it)
            if (it == null)
                return@Observer

            list.clear()
            val tempList = mutableListOf<ToDoListData>()
            it.forEach {
                tempList.add(
                    ToDoListData(
                        title = it.title,
                        date = it.date,
                        time = it.time,
                        indexDb = it.id,
                        isShow = it.isShow
                    )
                )

            }

            list.addAll(tempList)
            listAdapter.notifyDataSetChanged()
            viewModel.position = -1;

            viewModel.toDoList.value = null
        })

        viewModel.toDoListData.observe(this, androidx.lifecycle.Observer {
            if (viewModel.position != -1) {
                list.set(viewModel.position, it)
                listAdapter.notifyItemChanged(viewModel.position)
            } else {
                list.add(it)
                listAdapter.notifyDataSetChanged()
            }
            viewModel.position = -1;
        })

        binding.etdate.setOnClickListener {

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                logEvent("USER_PICKED_DATE")
                // Display Selected date in textbox
                binding.etdate.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                viewModel.month = monthOfYear
                viewModel.year = year
                viewModel.day = dayOfMonth
            }, year, month, day)

            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()

        }
        binding.etTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                logEvent("USER_PICKED_TIME")
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                this.cal.set(Calendar.HOUR_OF_DAY, hour)
                this.cal.set(Calendar.MINUTE, minute)

                viewModel.hour = hour
                viewModel.minute = minute

                binding.etTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }

            this.cal = cal
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
        checkNotificationPermission()
    }

    private fun checkNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
                return
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val launcher: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if(isGranted){
            Toast.makeText(this,"Permission granted.",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Permission denied.",Toast.LENGTH_SHORT).show()
        }
    }
    override fun onResume() {
        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
        logEvent("APP_CLOSE")
    }
    override fun onItemClick(v: View, position: Int) {
        logEvent("CLICKED_ON_ITEM")

        alert {
            message = list.get(position).title
            positiveButton("Edit") {
                viewModel.title.set(list.get(position).title)
                viewModel.date.set(list.get(position).date)
                viewModel.time.set(list.get(position).time)
                viewModel.position = position
                viewModel.index = list.get(position).indexDb
                binding.editText.isFocusable = true
            }
            negativeButton("Delete") {
                viewModel.delete(list.get(position).indexDb)
            }

        }.show()

    }

    override fun onStop() {
        super.onStop()
    }
}
