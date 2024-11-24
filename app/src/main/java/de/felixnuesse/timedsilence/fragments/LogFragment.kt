package de.felixnuesse.timedsilence.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.databinding.FragmentLogsBinding
import de.felixnuesse.timedsilence.model.database.room.LogDatabase
import de.felixnuesse.timedsilence.ui.LogEntryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class LogFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var _binding: FragmentLogsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewManager = LinearLayoutManager(view.context)
        refreshLogs()

        binding.labelLogFragment.setOnClickListener {
            refreshLogs()
        }

    }


    fun refreshLogs() {
        runBlocking {
            launch(Dispatchers.IO) {

                val logs = LogDatabase.get(requireContext()).logEntryDao()
                var logEntries = logs.getAllLoggedStates()

                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    viewAdapter = LogEntryAdapter(logEntries)
                    binding.logList.apply {
                        setHasFixedSize(false)
                        layoutManager = viewManager
                        adapter = viewAdapter
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
