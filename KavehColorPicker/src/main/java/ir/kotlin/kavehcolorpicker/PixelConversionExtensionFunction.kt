package ir.kotlin.kavehcolorpicker

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View

/**
 * Extension function for converting a dp value to pixels.
 * This extension runs inside a view.
 * @param number Number to be converted.
 */
internal fun View.dp(number: Number): Float {
    val metric =
        getDisplayMetric(context)

    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, number.toFloat(), metric)
}

/**
 * This method returns DisplayMetric of current device.
 * If Context is null the default system display metric would be returned which has default
 * density etc...
 */
internal fun getDisplayMetric(context: Context?): DisplayMetrics {
    return if (context != null) context.resources.displayMetrics else Resources.getSystem().displayMetrics
}


