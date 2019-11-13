package de.felixnuesse.timedsilence.fragments

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.felixnuesse.timedsilence.Constants
import android.database.Cursor
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.TextKeyListener.clear
import de.felixnuesse.timedsilence.dialogs.CalendarDialog
import de.felixnuesse.timedsilence.dialogs.ScheduleDialog
import de.felixnuesse.timedsilence.handler.CalendarHandler
import de.felixnuesse.timedsilence.handler.WifiHandler
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.CalendarListAdapter
import de.felixnuesse.timedsilence.ui.ScheduleListAdapter
import kotlinx.android.synthetic.main.calendar_event_fragment.*
import kotlinx.android.synthetic.main.time_fragment.*
import kotlinx.android.synthetic.main.wifi_connected_fragment.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarEventFragment : Fragment() {

    companion object {
        fun newInstance() = CalendarEventFragment()

        var nameOfEvent = ArrayList<String>()
        var startDates = ArrayList<String>()
        var endDates = ArrayList<String>()
        var descriptions = ArrayList<String>()

    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(de.felixnuesse.timedsilence.R.layout.calendar_event_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val calHandler = CalendarHandler(view.context)


        checkContainer(calHandler, view.context)

        button_calendar_fragment.setOnClickListener {
            Log.e(Constants.APP_NAME, "CalendarFragment: Add new!")
            CalendarDialog(view.context, this, calHandler).show()
        }

        val db = DatabaseHandler(view.context)

        Log.e(Constants.APP_NAME, "CalendarFragment: DatabaseResuluts: Size: "+db.getAllCalendarEntries().size)



        viewManager = LinearLayoutManager(view.context)
        viewAdapter = CalendarListAdapter(db.getAllCalendarEntries(), calHandler)

        calendar_fragment_recylcer_list_view.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

    }

    private fun checkContainer(calHandler: CalendarHandler, context: Context){

        val emtpyMessage = CalendarEmptyContainer
        val permissionMessage = CalendarPermissionContainer
        val listContainer = CalendarListContainer




        emtpyMessage.visibility = View.GONE
        permissionMessage.visibility = View.GONE
        listContainer.visibility = View.GONE

        val allowed = CalendarHandler.hasCalendarReadPermission(context)
        val size =calHandler.getCalendars().size

        if(allowed && size!=0){
            emtpyMessage.visibility = View.GONE
            permissionMessage.visibility = View.GONE
            listContainer.visibility = View.VISIBLE
        }else if(size==0){
            emtpyMessage.visibility = View.VISIBLE
            permissionMessage.visibility = View.GONE
            listContainer.visibility = View.GONE
        }else if(!CalendarHandler.hasCalendarReadPermission(context)) {
            emtpyMessage.visibility = View.GONE
            permissionMessage.visibility = View.VISIBLE
            listContainer.visibility = View.GONE
        }
    }

    fun saveCalendar(context: Context, co: CalendarObject){
        val db = DatabaseHandler(context)
        db.createCalendarEntry(co)
        viewAdapter = CalendarListAdapter(db.getAllCalendarEntries(), CalendarHandler(context))

        calendar_fragment_recylcer_list_view.apply {
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
