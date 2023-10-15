package de.felixnuesse.timedsilence.fragments.graph

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.skydoves.balloon.*
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.FragmentGraphBinding
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.handler.calculator.HeadsetHandler
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import kotlin.math.absoluteValue


class GraphFragment : Fragment() {

    private val BAR_WIDTH = 12
    private lateinit var viewObject: View



    private lateinit var last_text: GraphBarVolumeSwitchElement
    private lateinit var current_text: GraphBarVolumeSwitchElement
    private var mInitiallyAdded = false

    private var mBalloon: Balloon? = null

    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraphBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewObject = view

        binding.imageviewLegendLoudHelp.setOnClickListener {
            handleTooltipRight(requireContext(), getString(R.string.volume_setting_loud_help), it)
        }
        binding.imageviewLegendSilentHelp.setOnClickListener {
            handleTooltipRight(requireContext(), getString(R.string.volume_setting_silent_help), it)
        }
        binding.imageviewLegendVibrateHelp.setOnClickListener {
            handleTooltipRight(requireContext(), getString(R.string.volume_setting_vibrate_help), it)
        }
        binding.imageviewLegendUnsetHelp.setOnClickListener {
            handleTooltipRight(requireContext(), getString(R.string.volume_setting_unset_help), it)
        }

        binding.imageviewHeadphonesConnected.setOnClickListener {
            var builder = getTooltip(requireContext(), getString(R.string.headphones_help))
            builder.setArrowPosition(0.75f)
            it.showAlignTop(builder.build())
        }

        if(!HeadsetHandler.headphonesConnected(view.context)){
            binding.imageviewHeadphonesConnected.visibility=View.INVISIBLE
            binding.textfieldHeadsetConnected.visibility=View.INVISIBLE
        }

        if(!SharedPreferencesHandler.getPref(view.context, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET_DEFAULT)){
            binding.imageviewHeadphonesConnected.visibility=View.INVISIBLE
            binding.textfieldHeadsetConnected.visibility=View.INVISIBLE
        }

        view.viewTreeObserver.addOnGlobalLayoutListener {
            if(!mInitiallyAdded) {
                binding.relLayout.post {
                    view?.let { buildGraph(it.context, binding.relLayout) }
                }
                mInitiallyAdded = true
            }
        }
    }

    fun buildGraph(context:Context, relLayout: RelativeLayout){

        val thread = GraphFragmentThread(requireContext())
        val list = thread.doIt(requireContext()) as ArrayList

        //isFirst and isLast is reversed because we traverse the list backwards!
        var isLastElem=false
        var isFirstElem=true
        val revL=list.asReversed()
        for (elem in revL){

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


            setLastTextview(elem)
            createBarElem(context, relLayout, resources.getColor(id), elem, isFirstElem)
            isLastElem=false
            isFirstElem=false
        }

        setLegendColor(context, R.color.color_graph_unset, binding.imageViewLegendUnset)
        setLegendColor(context, R.color.color_graph_silent, binding.imageViewLegendSilent)
        setLegendColor(context, R.color.color_graph_vibrate, binding.imageViewLegendVibrate)
        setLegendColor(context, R.color.color_graph_loud, binding.imageViewLegendLoud)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun createBarElem(context: Context, relativeLayout: RelativeLayout, color: Int, gbvse: GraphBarVolumeSwitchElement, isFirst: Boolean){
        val viewid = View.generateViewId()
        val shapeview = getShape(context, color, gbvse.getBarLenghtInPercent(), viewid, (relativeLayout as View).height)

        shapeview.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action){
                MotionEvent.ACTION_UP -> {
                    view.showAsDropDown(
                        getTooltip(context, gbvse.state.getReason(), ArrowOrientation.LEFT).build(),
                        getSizeInDP(BAR_WIDTH),
                        -1*shapeview.height+motionEvent.y.toInt()-30 // add a slight offset to account for the "rounding" which is not actually touchable
                    )
                }
            }
            return@OnTouchListener false
        })

        val text = getTextForShape(context, gbvse.text, gbvse.state, viewid)

        relativeLayout.addView(shapeview)

        val t = relativeLayout.getChildAt(relativeLayout.childCount-1)
        if(isViewOverlapping() || gbvse.getBarLenghtInPercent() == 0.0F){
            text.visibility=View.GONE
        }

        //remember, this is actually the last element!
        if(!isFirst){
            relativeLayout.addView(text)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun getTextForShape(context: Context, text: String, volume: VolumeState, setTo: Int):View{
        val textView = TextView(context)

        var text_addition=""
        when (volume.state) {
            Constants.TIME_SETTING_SILENT -> text_addition = "Silent"
            Constants.TIME_SETTING_VIBRATE -> text_addition = "Vibrate"
            Constants.TIME_SETTING_LOUD -> text_addition = "Loud"
            Constants.TIME_SETTING_UNSET -> text_addition = "Unset"
        }

        textView.setText(" - $text: $text_addition")
        textView.setTypeface(null, Typeface.ITALIC)


        val image = getTooltipIcon(context, volume.getReason())

        val rootView = LinearLayout(context)
        val imageViewParam = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        imageViewParam.setMargins(getSizeInDP(20),0,0,getSizeInDP(-8))

        rootView.layoutParams = imageViewParam
        imageViewParam.addRule(RelativeLayout.RIGHT_OF, setTo)
        imageViewParam.addRule(RelativeLayout.ALIGN_BOTTOM, setTo)

        rootView.addView(textView)
        rootView.addView(image)

        return rootView

    }

    private fun getShape(context: Context, color: Int, lengthInPercent: Float, id: Int, maxLengthPixel: Int): View {
        val csl = ColorStateList.valueOf(color)
        val shapeDrawable = context.getDrawable(R.drawable.shape_drawable_bar) as GradientDrawable
        shapeDrawable.color = csl

        var lengthOfBarInPixel = (lengthInPercent*maxLengthPixel*0.9).toInt()

        if(lengthOfBarInPixel<convertPixelToDP(12)){
            lengthOfBarInPixel=convertPixelToDP(12)
        }

        //detach the drawable from it's source so it can be changed independently
        shapeDrawable.mutate()
        shapeDrawable.setSize(getSizeInDP(BAR_WIDTH), lengthOfBarInPixel)
        shapeDrawable.intrinsicHeight
        val image = ImageView(context)
        image.setImageDrawable(shapeDrawable)

        image.id=id
        return image
    }

    // Todo: this is highly suspect
    private fun getSizeInDP(size: Int): Int{
        //https://www.mysamplecode.com/2011/10/android-set-padding-programmatically-in.html
        return (size * resources.displayMetrics.density + 0.5f).toInt()
    }

    private fun convertPixelToDP(pixel: Int): Int {
        return pixel.div(resources.displayMetrics.density).toInt()
    }

    private fun setLegendColor(context: Context, color: Int, image: ImageView): View {
        val shapeDrawable = resources.getDrawable(R.drawable.shape_drawable_bar_legend) as GradientDrawable
        shapeDrawable.color = ColorStateList.valueOf(resources.getColor(color))
        image.setImageDrawable(shapeDrawable)

        return image
    }

    private fun setLastTextview(time: GraphBarVolumeSwitchElement){
        if(::current_text.isInitialized){
            last_text=current_text
        }
        current_text=time
    }

    /**
     * Required to dismiss old tooltips when a new was opened
     */
    fun handleTooltip(context: Context, tooltip: String, view: View){
        view.showAlignTop(getTooltip(context, tooltip).build())
    }

    fun handleTooltipRight(context: Context, tooltip: String, view: View){
        view.showAlignLeft(getTooltip(context, tooltip, ArrowOrientation.RIGHT).build())
    }


    fun getTooltip(context: Context, tooltip: String): Balloon.Builder {
        return getTooltip(context, tooltip, ArrowOrientation.BOTTOM)
    }

    fun getTooltip(context: Context, tooltip: String, orientation: ArrowOrientation): Balloon.Builder {
        var balloon = Balloon.Builder(context)
        balloon.setArrowSize(10)
        balloon.setArrowPosition(0.5f)
        balloon.setCornerRadius(4f)
        //balloon.setHeight(getSizeInDP(12))
        balloon.paddingLeft=getSizeInDP(8)
        balloon.paddingRight=getSizeInDP(8)
        balloon.paddingTop=getSizeInDP(8)
        balloon.paddingBottom=getSizeInDP(8)
        balloon.setAlpha(0.9f)
        balloon.setText(tooltip)
        balloon.setArrowOrientation(orientation)
        balloon.setBalloonAnimation(BalloonAnimation.OVERSHOOT)
        balloon.setBackgroundColor(ContextCompat.getColor(context, R.color.tooltip_background))
        balloon.setDismissWhenTouchOutside(true)
        return balloon
    }

    private fun getTooltipIcon(context: Context, tooltip: String): View {
        val imageView = ImageView(context)

        var d = ContextCompat.getDrawable(context, R.drawable.icon_help)
        if (d != null) {
            d.applyTheme(context.theme)
            imageView.setImageDrawable(d)
        }

        val imageViewParam = RelativeLayout.LayoutParams(
            getSizeInDP(16),
            getSizeInDP(16)
        )
        imageViewParam.setMargins(getSizeInDP(4),getSizeInDP(2),0, 0)

        imageView.layoutParams = imageViewParam

        imageView.setOnClickListener { it ->
            handleTooltip(context, tooltip, it)
        }

        return imageView
    }

    private fun isViewOverlapping(): Boolean {

        if(!::last_text.isInitialized){
            return false
        }
        if((last_text.minuteOfDay-current_text.minuteOfDay).absoluteValue>30){

            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
