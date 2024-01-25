package de.felixnuesse.timedsilence.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.databinding.FragmentBluetoothBinding
import de.felixnuesse.timedsilence.handler.calculator.HeadsetHandler
import de.felixnuesse.timedsilence.ui.BluetoothListAdapter


class BluetoothFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var _binding: FragmentBluetoothBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBluetoothBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewManager = LinearLayoutManager(view.context)

        viewAdapter = BluetoothListAdapter(HeadsetHandler.getPairedDevicesWithDatabaseState(view.context), view.context)

        binding.bluetoothFragmentRecylcerListView.apply {
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
