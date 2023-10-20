package de.felixnuesse.timedsilence.util

import android.content.Context

class SizeUtil {


    companion object {
        // Todo: this is highly suspect
        fun getSizeInDP(context: Context, size: Int): Int{
            //https://www.mysamplecode.com/2011/10/android-set-padding-programmatically-in.html
            return (size * context.resources.displayMetrics.density + 0.5f).toInt()
        }

        fun convertPixelToDP(context: Context, pixel: Int): Int {
            return pixel.div(context.resources.displayMetrics.density).toInt()
        }
    }


}