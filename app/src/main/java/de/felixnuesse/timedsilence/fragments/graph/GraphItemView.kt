package de.felixnuesse.timedsilence.fragments.graph

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.showAlignTop
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.databinding.CustomuiGraphitemBinding
import de.felixnuesse.timedsilence.util.SizeUtil.Companion.getSizeInDP

class GraphItemView: LinearLayout {


    private var binding = CustomuiGraphitemBinding.inflate(LayoutInflater.from(context), this, true)
    private var mColor: ColorStateList

    // dont use
    constructor(context: Context): super(context) {
        mColor = ColorStateList.valueOf(0)
    }

    // dont use
    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        mColor = ColorStateList.valueOf(0)
    }

    // dont use
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        mColor = ColorStateList.valueOf(0)
    }

    constructor(context: Context, color: Int, isFirst: Boolean, overlapColor: Int = -1) : super(context) {
        layoutParams = LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        mColor = ColorStateList.valueOf(color)

        if(overlapColor != -1) {
            var shape = AppCompatResources.getDrawable(context, R.drawable.shape_drawable_bar_center) as GradientDrawable
            shape.mutate()
            shape.color = ColorStateList.valueOf(overlapColor)
            binding.below.setImageDrawable(shape)

        } else {
            binding.below.visibility = View.INVISIBLE
        }

        var shapeDrawable = AppCompatResources.getDrawable(context, R.drawable.shape_drawable_bar_bottomonly) as GradientDrawable
        if(isFirst) {
            shapeDrawable = AppCompatResources.getDrawable(context, R.drawable.shape_drawable_bar) as GradientDrawable
        }

        shapeDrawable.mutate()
        shapeDrawable.color = mColor
        binding.bottom.setImageDrawable(shapeDrawable)

    }

    fun updateVisibility(duration: Long) {
        if(duration<10) {
            binding.textContainer.visibility = View.GONE
        }
    }

    fun setTooltip(reason: String) {
        binding.textContainer.setOnClickListener {
            handleTooltip(this.context, reason, it)
        }
    }

    /**
     * Required to dismiss old tooltips when a new was opened
     */
    fun handleTooltip(context: Context, tooltip: String, view: View){
        view.showAlignTop(getTooltip(context, tooltip).build())
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
        balloon.paddingLeft=getSizeInDP(context, 8)
        balloon.paddingRight=getSizeInDP(context, 8)
        balloon.paddingTop=getSizeInDP(context, 8)
        balloon.paddingBottom=getSizeInDP(context, 8)
        balloon.setAlpha(0.9f)
        balloon.setText(tooltip)
        balloon.setArrowOrientation(orientation)
        balloon.setBalloonAnimation(BalloonAnimation.OVERSHOOT)
        balloon.setBackgroundColor(ContextCompat.getColor(context, R.color.tooltip_background))
        balloon.setDismissWhenTouchOutside(true)
        return balloon
    }

    fun setText(s: String) {
        binding.label.text = s
    }

    fun changeAnnotationVisibility(visible: Boolean) {
        if(visible) {
            binding.textContainer.visibility = View.VISIBLE
        } else {
            binding.textContainer.visibility = View.GONE
        }
    }

    fun setOnBarClick(listener: OnClickListener){
        binding.barElement.setOnClickListener(listener)
    }
}