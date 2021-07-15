package de.felixnuesse.timedsilence.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.felixnuesse.timedsilence.Constants
import androidx.recyclerview.widget.RecyclerView
import de.felixnuesse.timedsilence.dialogs.KeywordDialog
import de.felixnuesse.timedsilence.model.data.KeywordObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.KeywordListAdapter
import de.felixnuesse.timedsilence.ui.custom.NestedRecyclerManager
import kotlinx.android.synthetic.main.calendar_event_fragment.*


class KeywordFragment : Fragment() {

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(de.felixnuesse.timedsilence.R.layout.calendar_keyword_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        button_calendar_fragment.setOnClickListener {
            KeywordDialog(view.context, this).show()
            Log.e(Constants.APP_NAME, "CalendarKeywordFragment: Add new!")
        }

        val db = DatabaseHandler(view.context)

        Log.e(Constants.APP_NAME, "CalendarKeywordFragment: DatabaseResuluts: Size: "+db.getKeywords().size)

        viewManager = NestedRecyclerManager(view.context)
        viewAdapter = KeywordListAdapter(db.getKeywords())

        calendar_fragment_recylcer_list_view.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }

    }

    fun saveKeyword(context: Context, keywordObject: KeywordObject){
        val db = DatabaseHandler(context)
        db.createKeyword(keywordObject)
        viewAdapter = KeywordListAdapter(db.getKeywords())

        calendar_fragment_recylcer_list_view.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }
}
