package de.felixnuesse.timedsilence.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.databinding.FragmentTimeBinding
import de.felixnuesse.timedsilence.dialogs.ScheduleDialog
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.ScheduleListAdapter
import de.felixnuesse.timedsilence.ui.custom.NestedRecyclerManager


class TimeFragment : Fragment() {

    companion object {
        private const val TAG = "TimeFragment"
    }

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var _binding: FragmentTimeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonTimeFragment.setOnClickListener {
            Log.e(TAG, "TimeFragment: Add new!")
            ScheduleDialog(view.context, this).show()
        }

        val db = DatabaseHandler(view.context)
        Log.e(TAG, "TimeFragment: DatabaseResuluts: Size: "+db.getAllSchedules().size)

        viewManager = NestedRecyclerManager(view.context)
        viewAdapter = ScheduleListAdapter(db.getAllSchedules())

        binding.timeFragmentRecylcerListView.apply {
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

        binding.timeFragmentRecylcerListView.apply {
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
}
