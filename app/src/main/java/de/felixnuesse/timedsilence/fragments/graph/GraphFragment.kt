package de.felixnuesse.timedsilence.fragments.graph

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.graph_fragment.*
import de.felixnuesse.timedsilence.R
import android.content.res.ColorStateList
import android.util.Log
import android.widget.TextView
import android.widget.RelativeLayout
import de.felixnuesse.timedsilence.Constants
import kotlin.properties.Delegates


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


        val thread = GraphFragmentThread(context!!, bar_list)

        val list = thread.doIt(context!!,bar_list)

        for (elem in list.asReversed()){

            var state = elem.Volume


            var id= R.color.colorAccent
            when (state) {
                Constants.TIME_SETTING_SILENT -> id= R.color.colorFab_stopped
                Constants.TIME_SETTING_VIBRATE -> id= R.color.colorFab_paused
                Constants.TIME_SETTING_LOUD -> id= R.color.colorFab_started
            }

            Log.e("app", "run ${elem.text}: ${state}")

            createBarElem(view, view.findViewById(R.id.rel_layout), resources.getColor(id), elem.getBarLenght(),  elem.text)

        }

        //createBarElem(view, rel_layout, resources.getColor(R.color.design_default_color_primary), 1.0f,  "test1")
        //createBarElem(view, rel_layout, resources.getColor(R.color.colorFab_stopped), 0.75f,  "test2")
        //createBarElem(view, rel_layout, resources.getColor(R.color.colorFab_started), 0.50f,  "test3")
        //createBarElem(view, rel_layout, resources.getColor(R.color.colorAccent), 0.25f,  "test4")

        //val thread = GraphFragmentThread(context!!, bar_list)
       // thread.start() // Will output: Thread[Thread-0,5,main] has run.    val runnable = SimpleRunnable()
       // val thread1 = Thread(GraphFragmentThread.SimpleRunnable(context!!, bar_list))
       // thread1.start() // Will output: Thread[Thread-1,5,main] has

        //thread.doIt(context!!,bar_list)
    }


    private fun createBarElem(view: View, relativeLayout: RelativeLayout, color: Int, len: Float, text: String){
        val viewid=View.generateViewId()
        val shapeview = getShape(view,color, len, viewid)
        val text= getTextForShape(view, text, viewid)

        relativeLayout.addView(shapeview)
        relativeLayout.addView(text)
    }

    private fun getTextForShape(view: View, text: String, setTo: Int):View{

        val textView = TextView(view.context)
        textView.setText(text)

        val imageViewParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        imageViewParam.setMargins(10,0,0,0)

        textView.setLayoutParams(imageViewParam)
        imageViewParam.addRule(RelativeLayout.RIGHT_OF, setTo)
        imageViewParam.addRule(RelativeLayout.ALIGN_BOTTOM, setTo)
        return textView

    }

    private fun getShape(view: View, color: Int, len: Float, id: Int): View {
        val colorInt = color
        val csl = ColorStateList.valueOf(colorInt)

        val shapeDrawable = resources.getDrawable(R.drawable.drawable_bar) as GradientDrawable
        shapeDrawable.color = csl


        //https://www.mysamplecode.com/2011/10/android-set-padding-programmatically-in.html
        val scale = resources.displayMetrics.density
        val _12dp = (10 * scale + 0.5f).toInt()
        val _100dp = (500 * scale + 0.5f).toInt()

        shapeDrawable.setSize(_12dp, (len*_100dp).toInt())

        val image: ImageView = ImageView(view.context)
        image.setImageDrawable(shapeDrawable)

        image.id=id
        return image
    }

}
