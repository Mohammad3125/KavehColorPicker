package ir.kotlin.kavehcolorpicker

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet

class KavehColorAlphaSlider(context: Context, attributeSet: AttributeSet?) :
    KavehColorSlider(context, attributeSet) {

    constructor(context: Context) : this(context, null)

    private lateinit var alphaLinearGradient: LinearGradient

    var selectedColor = Color.RED
        set(value) {
            if (field != value) {
                field = value
                initializeSliderPaint()
                invalidate()
            }
        }

    private var currentAlpha = 1f

    var alphaValue: Float = 1f
        set(value) {
            field = value
            isSliderChangingState = true
            circleXFactor = value
            calculateBounds(width.toFloat(),height.toFloat())
            invalidate()
        }
        get() = currentAlpha


    private var onAlphaChanged: ((alpha: Float) -> Unit)? = null
    private var onAlphaChangedListener: OnAlphaChangedListener? = null


    override fun onCirclePositionChanged(circlePositionX: Float, circlePositionY: Float) {

        currentAlpha = calculateAlphaAt(circlePositionX)

        circleColor =
            Color.argb(
                (255 * currentAlpha).toInt(),
                Color.red(selectedColor),
                Color.green(selectedColor),
                Color.blue(selectedColor)
            )

        callListeners(currentAlpha)

        invalidate()

    }

    override fun calculateBounds(targetWidth: Float, targetHeight: Float) {
        super.calculateBounds(targetWidth, targetHeight)

        currentAlpha = calculateAlphaAt(circleX).coerceIn(0f, 1f)

        circleColor = calculateCircleColor()

    }

    private fun calculateCircleColor(): Int {
        return Color.argb(
            (255 * currentAlpha).toInt(),
            Color.red(selectedColor),
            Color.green(selectedColor),
            Color.blue(selectedColor)
        )
    }

    private fun calculateAlphaAt(ex: Float): Float {
        return (ex - drawingStart) / (widthF - drawingStart)
    }

    override fun initializeSliderPaint() {
        alphaLinearGradient =
            LinearGradient(
                drawingStart,
                0f,
                widthF,
                0f,
                Color.argb(
                    0,
                    Color.red(selectedColor),
                    Color.green(selectedColor),
                    Color.blue(selectedColor)
                ),
                selectedColor,
                Shader.TileMode.MIRROR
            )

        circleColor =
            Color.argb(
                (255 * currentAlpha).toInt(),
                Color.red(selectedColor),
                Color.green(selectedColor),
                Color.blue(selectedColor)
            )


        linePaint.shader = alphaLinearGradient
    }

    override fun onSaveInstanceState(): Parcelable {
        return (super.onSaveInstanceState() as Bundle).apply {
            putInt(SELECTED_COLOR_KEY, selectedColor)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        selectedColor = (state as Bundle).getInt(SELECTED_COLOR_KEY)
        super.onRestoreInstanceState(state)
    }

    fun setOnAlphaChangedListener(onAlphaChangedListener: OnAlphaChangedListener) {
        this.onAlphaChangedListener = onAlphaChangedListener
    }

    fun setOnAlphaChangedListener(onAlphaChangedListener: ((alpha: Float) -> Unit)) {
        onAlphaChanged = onAlphaChangedListener
    }

    private fun callListeners(alpha: Float) {
        onAlphaChanged?.invoke(alpha)
        onAlphaChangedListener?.onAlphaChanged(alpha)
    }


    interface OnAlphaChangedListener {
        fun onAlphaChanged(hue: Float)
    }

    companion object {
        private const val SELECTED_COLOR_KEY = "sel"
    }

}