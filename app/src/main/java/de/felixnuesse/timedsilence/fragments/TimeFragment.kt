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
import android.app.TimePickerDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.dialogs.ScheduleDialog
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.ScheduleListAdapter
import java.text.DateFormat
import java.util.*



class TimeFragment : Fragment() {

    companion object {
        fun newInstance() = TimeFragment()
    }

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
            ScheduleDialog(view.context, this, true).show()
        }



        val db = DatabaseHandler(view.context)

        Log.e(APP_NAME, "TimeFragment: DatabaseResuluts: Size: "+db.getAllSchedules().size)

        viewManager = LinearLayoutManager(view.context)
        viewAdapter = ScheduleListAdapter(db.getAllSchedules())

        time_fragment_recylcer_list_view.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }




    }

    fun saveSchedule(context: Context, so: ScheduleObject){
        val db = DatabaseHandler(context)
        db.createScheduleEntry(so)
        viewAdapter = ScheduleListAdapter(db.getAllSchedules())

        time_fragment_recylcer_list_view.apply {
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
