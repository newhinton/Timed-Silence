package de.felixnuesse.timedsilence.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.databinding.FragmentSchedulesBinding
import de.felixnuesse.timedsilence.dialogs.ScheduleDialog
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.ScheduleListAdapter
import de.felixnuesse.timedsilence.ui.custom.NestedRecyclerManager


class ScheduleFragment : Fragment() {

    companion object {
        private const val TAG = "TimeFragment"
    }

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var _binding: FragmentSchedulesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonTimeFragment.setOnClickListener {
            ScheduleDialog(view.context, this).show()
        }
        binding.buttonIcon.setOnClickListener {
            ScheduleDialog(view.context, this).show()
        }

        val db = DatabaseHandler(view.context)

        viewManager = NestedRecyclerManager(view.context)
        viewAdapter = ScheduleListAdapter(db.getAllSchedules())

        binding.timeFragmentRecylcerListView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun saveSchedule(context: Context, so: ScheduleObject){
        val db = DatabaseHandler(context)
        db.createScheduleEntry(so)
        viewAdapter = ScheduleListAdapter(db.getAllSchedules())

        binding.timeFragmentRecylcerListView.apply {
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
