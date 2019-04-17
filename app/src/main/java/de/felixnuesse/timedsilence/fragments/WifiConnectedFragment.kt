package de.felixnuesse.timedsilence.fragments

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.WifiHandler
import kotlinx.android.synthetic.main.wifi_connected_fragment.*


class WifiConnectedFragment : Fragment() {

    companion object {
        fun newInstance() = WifiConnectedFragment()
    }

    private lateinit var viewModel: WifiConnectedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.wifi_connected_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WifiConnectedViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttonRequestWifiPermissions.setOnClickListener {
            Log.e(Constants.APP_NAME, "WifiConnectedFragment: Request Location Permission!")
            WifiHandler.requestPermissions(view.context as Activity)
        }
    }


}
