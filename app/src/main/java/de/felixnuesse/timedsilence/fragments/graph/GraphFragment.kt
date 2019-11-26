package de.felixnuesse.timedsilence.fragments.graph

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.graph_fragment.*
import android.widget.TextView
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.VolumeCalculator
import java.time.Instant
import java.time.ZoneId


class GraphFragment : Fragment() {

    companion object {
        fun newInstance() = GraphFragment()
    }

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return  inflater.inflate(R.layout.graph_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //val thread = GraphFragmentThread(context!!, bar_list)
        //thread.start() // Will output: Thread[Thread-0,5,main] has run.    val runnable = SimpleRunnable()
        //val thread1 = Thread(GraphFragmentThread.SimpleRunnable(context!!, bar_list))
        //thread1.start() // Will output: Thread[Thread-1,5,main] has
    }

}
