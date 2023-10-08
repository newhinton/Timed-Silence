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
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.CalendarEventFragmentBinding
import de.felixnuesse.timedsilence.databinding.WifiConnectedFragmentBinding
import de.felixnuesse.timedsilence.dialogs.WifiDialog
import de.felixnuesse.timedsilence.handler.calculator.WifiHandler
import de.felixnuesse.timedsilence.model.data.WifiObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.WifiListAdapter
import de.felixnuesse.timedsilence.ui.custom.NestedRecyclerManager


class WifiConnectedFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var _binding: WifiConnectedFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WifiConnectedFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkContainer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonRequestWifiPermissions.setOnClickListener {
            Log.e(Constants.APP_NAME, "WifiConnectedFragment: Request Location Permission!")
            WifiHandler.requestPermissions(view.context as Activity)
            checkContainer()
        }

        binding.buttonWifiAddFragment.setOnClickListener {
            Log.e(APP_NAME, "WifiFragment: Add new!")
            //createSSIDDialog(view.context)
            WifiDialog(view.context, this).show()

        }


        val db = DatabaseHandler(view.context)

        Log.e(Constants.APP_NAME, "WifiFragment: DatabaseResuluts: Size: "+db.getAllWifiEntries().size)

        viewManager = NestedRecyclerManager(view.context)
        viewAdapter = WifiListAdapter(db.getAllWifiEntries())

        binding.wifiRecylcerListView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(false)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
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
