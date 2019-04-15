package de.felixnuesse.timedsilence.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.R
import kotlinx.android.synthetic.main.time_fragment.*
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import android.app.AlertDialog
import android.content.Context
import android.widget.TimePicker
import android.app.TimePickerDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.MyAdapter
import java.text.DateFormat
import java.util.*


class TimeFragment : Fragment() {

    companion object {
        fun newInstance() = TimeFragment()
    }


    var schedule_add_title =""
    var schedule_add_start_time_hour =0
    var schedule_add_start_time_min =0


    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager



    private lateinit var viewModel: TimeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {







        return inflater.inflate(R.layout.time_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TimeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button_time_fragment.setOnClickListener {
            Log.e(APP_NAME, "TimeFragment: Add new!")

            schedule_add_title =""


            createTitleDialog(view.context)


        }



        val db = DatabaseHandler(view.context)

        Log.e(APP_NAME, "test "+db.getAllSchedules().size)

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = MyAdapter(db.getAllSchedules())

        my_recycler_view.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }




    }


    fun createTitleDialog(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Title")

        // Set up the input
        val input = EditText(context)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which ->
                createStartDialog(context, input.text.toString())

            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    fun createStartDialog(context: Context, title: String){


        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(context,
            TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                schedule_add_start_time_hour=selectedHour
                schedule_add_start_time_min=selectedMinute
                createEndDialog(context, title, (selectedHour*60*60*1000+selectedMinute*60*1000).toLong())
            }, hour, minute, true
        )//Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    fun createEndDialog(context: Context, title: String, start_Time: Long){


        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(context,
            TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                schedule_add_start_time_hour=selectedHour
                schedule_add_start_time_min=selectedMinute
                createSettingDialog(context, title,start_Time, (selectedHour*60*60*1000+selectedMinute*60*1000).toLong())
            }, hour, minute, true
        )//Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    fun createSettingDialog(context: Context, title: String, start_Time: Long, end_Time: Long){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("setting (sil 1 vib 2 loud 3)")

        // Set up the input
        val input = EditText(context)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which ->

                val df = DateFormat.getTimeInstance()
                df.timeZone= TimeZone.getTimeZone("UTC")


                Log.e(APP_NAME, "TimeFragment: Schedulebuilder: A: "+title)
                Log.e(APP_NAME, "TimeFragment: Schedulebuilder: B: "+df.format(start_Time))
                Log.e(APP_NAME, "TimeFragment: Schedulebuilder: C: "+df.format(end_Time))
                Log.e(APP_NAME, "TimeFragment: Schedulebuilder: D: "+input.text.toString())

                DatabaseHandler(context).createEntry(ScheduleObject(title,start_Time,end_Time, input.text.toString().toInt(),0))

            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }
}
