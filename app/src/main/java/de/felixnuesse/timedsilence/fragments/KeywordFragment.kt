package de.felixnuesse.timedsilence.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.databinding.FragmentCalendarKeywordBinding
import de.felixnuesse.timedsilence.dialogs.KeywordDialog
import de.felixnuesse.timedsilence.model.data.KeywordObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.KeywordListAdapter
import de.felixnuesse.timedsilence.volumestate.calendar.DeviceCalendar


class KeywordFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var _binding: FragmentCalendarKeywordBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarKeywordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        checkContainer(DeviceCalendar(view.context), view.context)

        binding.buttonCalendarFragment.setOnClickListener {
            KeywordDialog(view.context, this).show()
        }
        binding.buttonIcon.setOnClickListener {
            KeywordDialog(view.context, this).show()
        }

        val db = DatabaseHandler(view.context)
        viewManager = LinearLayoutManager(view.context)
        viewAdapter = KeywordListAdapter(db.getKeywords())

        binding.calendarFragmentRecylcerListView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }

    fun saveKeyword(context: Context, keywordObject: KeywordObject){
        val db = DatabaseHandler(context)
        db.createKeyword(keywordObject)
        viewAdapter = KeywordListAdapter(db.getKeywords())

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Same as in Calendar Fragment.
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
        } else if(!allowed) {
            emtpyMessage.visibility = View.GONE
            permissionMessage.visibility = View.VISIBLE
            listContainer.visibility = View.GONE
            binding.permissionNotGrantedButton.setOnClickListener {
                DeviceCalendar.getCalendarReadPermission(context)
            }
        } else if(size==0){
            emtpyMessage.visibility = View.VISIBLE
            permissionMessage.visibility = View.GONE
            listContainer.visibility = View.GONE
        }
    }
}
