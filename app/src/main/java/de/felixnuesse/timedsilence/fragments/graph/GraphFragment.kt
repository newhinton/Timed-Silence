package de.felixnuesse.timedsilence.fragments.graph

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
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
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.FragmentGraphBinding
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.handler.calculator.HeadsetHandler
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.util.SizeUtil
import kotlin.math.absoluteValue


class GraphFragment : Fragment() {

    private lateinit var viewObject: View

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

        buildGraph(view.context, binding.relLayout)
    }

    fun buildGraph(context:Context, relLayout: LinearLayout){

        val thread = GraphFragmentThread(requireContext())
        val list = thread.doIt(requireContext()) as ArrayList

        var lastTime = 0
        for (elem in list){
            var state = elem.Volume

            var id= R.color.color_graph_unset
            when (state) {
                TIME_SETTING_SILENT -> id = R.color.color_graph_silent
                TIME_SETTING_VIBRATE -> id = R.color.color_graph_vibrate
                TIME_SETTING_LOUD -> id = R.color.color_graph_loud
            }

            relLayout.addView(createBarElem(context, lastTime, elem.minuteOfDay, resources.getColor(id), elem))
            lastTime = elem.minuteOfDay
        }

        setLegendColor(R.color.color_graph_unset, binding.imageViewLegendUnset)
        setLegendColor(R.color.color_graph_silent, binding.imageViewLegendSilent)
        setLegendColor(R.color.color_graph_vibrate, binding.imageViewLegendVibrate)
        setLegendColor(R.color.color_graph_loud, binding.imageViewLegendLoud)
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun createBarElem(context: Context, lastTime: Int, now: Int, color: Int, gbvse: GraphBarVolumeSwitchElement): LinearLayout {


        var outerLayout = LinearLayout(context)
        val outerLayoutParams = LinearLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            (now - lastTime).toFloat()
        )

        outerLayout.layoutParams = outerLayoutParams
        val item = GraphItemView(context.applicationContext)

        item.setTooltip(gbvse.state.getReason())


        var text_addition=""
        when (gbvse.Volume) {
            TIME_SETTING_SILENT -> text_addition = "Silent"
            TIME_SETTING_VIBRATE -> text_addition = "Vibrate"
            TIME_SETTING_LOUD -> text_addition = "Loud"
            TIME_SETTING_UNSET -> text_addition = "Unset"
        }

        item.setText(" - ${gbvse.text}: $text_addition")

        outerLayout.addView(item)
        outerLayout.setBackgroundColor(color)
        return outerLayout
    }

    private fun setLegendColor(color: Int, image: ImageView): View {
        val shapeDrawable = resources.getDrawable(R.drawable.shape_drawable_bar_legend) as GradientDrawable
        shapeDrawable.color = ColorStateList.valueOf(resources.getColor(color))
        image.setImageDrawable(shapeDrawable)

        return image
    }


    /**
     * Required to dismiss old tooltips when a new was opened
     */
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
        balloon.paddingLeft=SizeUtil.getSizeInDP(context, 8)
        balloon.paddingRight=SizeUtil.getSizeInDP(context, 8)
        balloon.paddingTop=SizeUtil.getSizeInDP(context, 8)
        balloon.paddingBottom=SizeUtil.getSizeInDP(context, 8)
        balloon.setAlpha(0.9f)
        balloon.setText(tooltip)
        balloon.setArrowOrientation(orientation)
        balloon.setBalloonAnimation(BalloonAnimation.OVERSHOOT)
        balloon.setBackgroundColor(ContextCompat.getColor(context, R.color.tooltip_background))
        balloon.setDismissWhenTouchOutside(true)
        return balloon
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
