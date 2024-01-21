package de.felixnuesse.timedsilence.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.databinding.FragmentCalendarEventBinding
import de.felixnuesse.timedsilence.dialogs.CalendarDialog
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.CalendarListAdapter
import de.felixnuesse.timedsilence.volumestate.calendar.DeviceCalendar


class CalendarFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var _binding: FragmentCalendarEventBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val calHandler = DeviceCalendar(view.context)


        checkContainer(calHandler, view.context)

        binding.buttonCalendarFragment.setOnClickListener {
            CalendarDialog(view.context, this, calHandler).show()
        }
        binding.buttonIcon.setOnClickListener {
            CalendarDialog(view.context, this, calHandler).show()
        }

        val db = DatabaseHandler(view.context)
        viewManager = LinearLayoutManager(view.context)
        viewAdapter = CalendarListAdapter(db.getAllCalendarEntries(), calHandler)

        binding.calendarFragmentRecylcerListView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

    }

    private fun checkContainer(calHandler: DeviceCalendar, context: Context){
        val emtpyMessage = binding.CalendarEmptyContainer
        val permissionMessage = binding.CalendarPermissionContainer
        val listContainer = binding.CalendarListContainer

        emtpyMessage.visibility = View.GONE
        permissionMessage.visibility = View.GONE
        listContainer.visibility = View.GONE

        val allowed = DeviceCalendar.hasCalendarReadPermission(context)
        val size = calHandler.getCalendars().size

        if(allowed && size!=0){
            emtpyMessage.visibility = View.GONE
            permissionMessage.visibility = View.GONE
            listContainer.visibility = View.VISIBLE
        }else if(size==0){
            emtpyMessage.visibility = View.VISIBLE
            permissionMessage.visibility = View.GONE
            listContainer.visibility = View.GONE
        }else if(!DeviceCalendar.hasCalendarReadPermission(context)) {
            emtpyMessage.visibility = View.GONE
            permissionMessage.visibility = View.VISIBLE
            listContainer.visibility = View.GONE
        }
    }

    fun saveCalendar(context: Context, co: CalendarObject){
        val db = DatabaseHandler(context)
        db.createCalendarEntry(co)
        viewAdapter = CalendarListAdapter(db.getAllCalendarEntries(),
            DeviceCalendar(context)
        )

        binding.calendarFragmentRecylcerListView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
