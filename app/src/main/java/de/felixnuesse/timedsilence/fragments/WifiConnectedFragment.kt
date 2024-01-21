package de.felixnuesse.timedsilence.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import de.felixnuesse.timedsilence.databinding.FragmentWifiConnectedBinding
import de.felixnuesse.timedsilence.dialogs.WifiDialog
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.handler.calculator.WifiHandler
import de.felixnuesse.timedsilence.model.data.WifiObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.WifiListAdapter


class WifiConnectedFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var _binding: FragmentWifiConnectedBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWifiConnectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkContainer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonRequestWifiPermissions.setOnClickListener {
            Log.e(TAG(), "WifiConnectedFragment: Request Location Permission!")
            WifiHandler.requestPermissions(view.context as Activity)
            checkContainer()
        }

        binding.buttonWifiAddFragment.setOnClickListener {
            WifiDialog(view.context, this).show()
        }
        binding.buttonIcon.setOnClickListener {
            WifiDialog(view.context, this).show()
        }


        val db = DatabaseHandler(view.context)
        viewManager = LinearLayoutManager(view.context)
        viewAdapter = WifiListAdapter(db.getAllWifiEntries())

        binding.wifiRecylcerListView.apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun checkContainer(){

        //val a = activity.findViewById(R.id.WifiContentContainer) as ConstraintLayout
        val a = binding.WifiContentContainer
        //val b = activity.findViewById(R.id.WifiRequestLocationContainer) as ConstraintLayout
        val b = binding.WifiRequestLocationContainer


        a.visibility = View.GONE
        b.visibility = View.VISIBLE

        if(WifiHandler.grantedWifiPermission(this.context as Activity)){
            a.visibility = View.VISIBLE
            b.visibility = View.GONE
        }
    }

    fun saveWifi(context: Context, wifiObj: WifiObject) {
        val db = DatabaseHandler(context)
        db.createWifiEntry(wifiObj)
        viewAdapter = WifiListAdapter(db.getAllWifiEntries())

        binding.wifiRecylcerListView.apply {
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
