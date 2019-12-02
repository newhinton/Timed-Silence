package de.felixnuesse.timedsilence.fragments.graph

import android.content.Context
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


class GraphFragment : Fragment() {

    companion object {
        fun newInstance() = GraphFragment()
    }

    private lateinit var viewObject: View
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var barList = ArrayList<Map<String, Any>>()


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
        viewObject = view
        buildGraph(view.context, view!!.findViewById(R.id.rel_layout))

    }

    fun buildGraph(context:Context, relLayout: RelativeLayout){

        val thread = GraphFragmentThread(context!!, bar_list)
        val list = thread.doIt(context!!,bar_list)

        //isFirst and isLast is reversed because we traverse the list backwards!
        var isLastElem=false
        var isFirstElem=true

        for (elem in list.asReversed()){


            var stateNext = -2
            //try to get the next element
            val nextIndex = (list.indexOf(elem)+1)
            if(nextIndex<list.size){
                stateNext= list.get(nextIndex).Volume
            }

            if(nextIndex==1){
                isLastElem=true
            }

            var state = elem.Volume


            var id= R.color.color_graph_unset
            var text_addition = "-2"
            when (state) {
                Constants.TIME_SETTING_SILENT -> id= R.color.color_graph_silent
                Constants.TIME_SETTING_VIBRATE -> id= R.color.color_graph_vibrate
                Constants.TIME_SETTING_LOUD -> id= R.color.color_graph_loud
            }

            when (stateNext) {
                Constants.TIME_SETTING_SILENT -> text_addition = "Silent"
                Constants.TIME_SETTING_VIBRATE -> text_addition = "Vibrate"
                Constants.TIME_SETTING_LOUD -> text_addition = "Loud"
                Constants.TIME_SETTING_UNSET -> text_addition = "Unset"
            }

            Log.e("app", "run ${elem.text}: ${elem.getBarLenght()}")

            if(isLastElem){
                id=R.color.color_graph_transparent
            }

            createBarElem(context, relLayout, resources.getColor(id), elem.getBarLenght(),  elem.text+" "+text_addition, isFirstElem)
            isLastElem=false
            isFirstElem=false
        }
    }


    private fun createBarElem(context: Context, relativeLayout: RelativeLayout, color: Int, len: Float, text: String, isFirst: Boolean){
        val viewid=View.generateViewId()
        val shapeview = getShape(context,color, len, viewid)
        val text= getTextForShape(context, text, viewid)


        val map = HashMap<String, Any>()
        map.put("ID", viewid)
        map.put("COLOR", color)
        map.put("LENGTH", len)
        barList.add(map)

        relativeLayout.addView(shapeview)
        //remember, this is actually the last element!
        if(!isFirst){
            relativeLayout.addView(text)

        }
    }

    private fun getTextForShape(context: Context, text: String, setTo: Int):View{

        val textView = TextView(context)
        textView.setText(text)

        val imageViewParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        imageViewParam.setMargins(getSizeInDP(20),0,0,getSizeInDP(-8))

        textView.setLayoutParams(imageViewParam)
        imageViewParam.addRule(RelativeLayout.RIGHT_OF, setTo)
        imageViewParam.addRule(RelativeLayout.ALIGN_BOTTOM, setTo)

        return textView

    }

    private fun getShape(context: Context, color: Int,len: Float, id: Int): View {
        val colorInt = color
        val csl = ColorStateList.valueOf(colorInt)

        val shapeDrawable = resources.getDrawable(R.drawable.drawable_bar) as GradientDrawable
        shapeDrawable.color = csl


        //https://www.mysamplecode.com/2011/10/android-set-padding-programmatically-in.html




        var lengthOfBar=len
        /*if(lengthOfBar>=0){
            //set the lenght to minimal 1%
            lengthOfBar=0.01F
        }*/


        var barlen = (lengthOfBar*getSizeInDP(500)).toInt()

        if(barlen<getSizeInDP(12)){
            barlen=getSizeInDP(12)
        }

        shapeDrawable.setSize(getSizeInDP(12),barlen)
        //shapeDrawable.setSize(getSizeInDP(12),0)

        val image: ImageView = ImageView(context)
        image.setImageDrawable(shapeDrawable)

        image.id=id
        return image
    }

    private fun getSizeInDP(size: Int): Int{
       // return size*5
        return (size * resources.displayMetrics.density + 0.5f).toInt()
    }
}
