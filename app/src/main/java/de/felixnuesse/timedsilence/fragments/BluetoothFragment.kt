package de.felixnuesse.timedsilence.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.databinding.FragmentBluetoothBinding
import de.felixnuesse.timedsilence.dialogs.BluetoothDialog
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
        setList(view.context)

        binding.buttonAddDevice.setOnClickListener {
            BluetoothDialog(view.context, this).show()
        }
    }

    override fun onResume() {
        super.onResume()
        setList(requireContext())
    }

    private fun setList(context: Context) {
        viewManager = LinearLayoutManager(context)

        val pairedDevices = HeadsetHandler.getPairedDevicesWithChangesInVolume(context)
        viewAdapter = BluetoothListAdapter(pairedDevices, context)

        binding.bluetoothFragmentRecylcerListView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        if(HeadsetHandler.getPairedDevices(context).size == 0) {
            binding.noPairedMessage.visibility = View.VISIBLE
            binding.bluetoothFragmentRecylcerListView.visibility = View.GONE
            binding.buttonAddDevice.visibility = View.GONE
        } else {
            binding.noPairedMessage.visibility = View.GONE
            binding.bluetoothFragmentRecylcerListView.visibility = View.VISIBLE
            binding.buttonAddDevice.visibility = View.VISIBLE
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun notifyChange() {
        setList(requireContext())
    }
}
