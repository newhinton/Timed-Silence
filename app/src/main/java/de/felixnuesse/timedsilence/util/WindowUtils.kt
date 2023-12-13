package de.felixnuesse.timedsilence.util

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.InsetDrawable
import android.view.Window
import androidx.appcompat.content.res.AppCompatResources
import de.felixnuesse.timedsilence.R

class WindowUtils {

    companion object {
        fun applyDialogPaddingFixForDarkmode(context: Context, window: Window) {
            // Fix Background color on dark mode
            if(context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                val drawable = AppCompatResources.getDrawable(context, R.drawable.shape_dialog_background)
                val inset = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
                    (window.decorView.background as InsetDrawable).opticalInsets.left
                } else{
                    50
                }
                window.decorView.background = InsetDrawable(drawable, inset)
            }
        }
    }
}