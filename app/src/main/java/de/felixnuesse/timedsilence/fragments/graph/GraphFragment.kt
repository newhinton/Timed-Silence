package de.felixnuesse.timedsilence.fragments.graph

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.graph_fragment.*
import de.felixnuesse.timedsilence.R
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.widget.TextView
import android.widget.RelativeLayout
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.handler.calculator.HeadsetHandler
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler


class GraphFragment : Fragment() {

    companion object {
        fun newInstance() = GraphFragment()
    }

    private lateinit var viewObject: View
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
        viewObject = view
        buildGraph(view.context, view!!.findViewById(R.id.rel_layout))


        if(!HeadsetHandler.headphonesConnected(view.context)){
            imageview_headphones_connected.visibility=View.INVISIBLE
            textfield_headset_connected.visibility=View.INVISIBLE
        }

        if(!SharedPreferencesHandler.getPref(view.context, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET_DEFAULT)){
            imageview_headphones_connected.visibility=View.INVISIBLE
            textfield_headset_connected.visibility=View.INVISIBLE
        }

    }

    fun buildGraph(context:Context, relLayout: RelativeLayout){

        val thread = GraphFragmentThread(context!!)
        val list = thread.doIt(context!!)

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
            when (state) {
                Constants.TIME_SETTING_SILENT -> id= R.color.color_graph_silent
                Constants.TIME_SETTING_VIBRATE -> id= R.color.color_graph_vibrate
                Constants.TIME_SETTING_LOUD -> id= R.color.color_graph_loud
            }


            if(isLastElem){
                id=R.color.color_graph_transparent
            }

            createBarElem(context, relLayout, resources.getColor(id), elem.getBarLenght(),  elem.text, elem.Volume, isFirstElem)
            isLastElem=false
            isFirstElem=false
        }

        setLegendColor(context, R.color.color_graph_unset, imageView_legend_unset)
        setLegendColor(context, R.color.color_graph_silent, imageView_legend_silent)
        setLegendColor(context, R.color.color_graph_vibrate, imageView_legend_vibrate)
        setLegendColor(context, R.color.color_graph_loud, imageView_legend_loud)
    }


    private fun createBarElem(context: Context, relativeLayout: RelativeLayout, color: Int, len: Float, text: String, volume: Int, isFirst: Boolean){
        val viewid=View.generateViewId()
        val shapeview = getShape(context,color, len, viewid)
        val text= getTextForShape(context, text, volume, viewid)

        relativeLayout.addView(shapeview)


        val t = relativeLayout.getChildAt(relativeLayout.childCount-1)
        if(isViewOverlapping(text, t)){
            text.visibility=View.GONE
        }

        //remember, this is actually the last element!
        if(!isFirst){
            relativeLayout.addView(text)

        }
    }

    private fun getTextForShape(context: Context, text: String, volume: Int,  setTo: Int):View{


        val textView = TextView(context)

        var text_addition=""
        when (volume) {
            Constants.TIME_SETTING_SILENT -> text_addition = "Silent"
            Constants.TIME_SETTING_VIBRATE -> text_addition = "Vibrate"
            Constants.TIME_SETTING_LOUD -> text_addition = "Loud"
            Constants.TIME_SETTING_UNSET -> text_addition = "Unset"
        }

        textView.setText(" - $text: $text_addition")
        textView.setTypeface(null, Typeface.ITALIC)

        val imageViewParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        imageViewParam.setMargins(getSizeInDP(20),0,0,getSizeInDP(-8))

        textView.layoutParams = imageViewParam
        imageViewParam.addRule(RelativeLayout.RIGHT_OF, setTo)
        imageViewParam.addRule(RelativeLayout.ALIGN_BOTTOM, setTo)

        return textView

    }

    private fun getShape(context: Context, color: Int,len: Float, id: Int): View {
        val colorInt = color
        val csl = ColorStateList.valueOf(colorInt)

        val shapeDrawable = resources.getDrawable(R.drawable.drawable_bar) as GradientDrawable
        shapeDrawable.color = csl

        var lengthOfBar=len

        var barlen = (lengthOfBar*getSizeInDP(500)).toInt()

        if(barlen<getSizeInDP(12)){
            barlen=getSizeInDP(12)
        }

        shapeDrawable.setSize(getSizeInDP(12),barlen)

        val image = ImageView(context)
        image.setImageDrawable(shapeDrawable)

        image.id=id
        return image
    }

    private fun getSizeInDP(size: Int): Int{
        //https://www.mysamplecode.com/2011/10/android-set-padding-programmatically-in.html
        return (size * resources.displayMetrics.density + 0.5f).toInt()
    }

    private fun setLegendColor(context: Context, color: Int, image: ImageView): View {
        val shapeDrawable = resources.getDrawable(R.drawable.drawable_bar_legend) as GradientDrawable
        shapeDrawable.color = ColorStateList.valueOf(resources.getColor(color))
        image.setImageDrawable(shapeDrawable)

        return image
    }

    private fun isViewOverlapping(firstView: View, secondView: View): Boolean {
        val firstPosition = IntArray(2)
        val secondPosition = IntArray(2)
        firstView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        firstView.getLocationOnScreen(firstPosition)
        secondView.getLocationOnScreen(secondPosition)
        val r = firstView.measuredWidth + firstPosition[0]
        val l = secondPosition[0]
        return r >= l && r != 0 && l != 0
    }
}
