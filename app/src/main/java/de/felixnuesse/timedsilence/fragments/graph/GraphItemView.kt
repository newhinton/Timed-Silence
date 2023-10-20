package de.felixnuesse.timedsilence.fragments.graph

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
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

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)  {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)  {}
    constructor(context: Context) : super(context) {}

    init {
    }


    private fun getPixelFromDp(dp: Int ): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    fun setTooltip(reason: String) {
        binding.imageView3.setOnClickListener {
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
        binding.textView3.text = s
    }
}