package ir.kotlin.kavehcolorpicker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Shader
import android.util.AttributeSet
import kotlin.math.floor

class KavehHueSlider(context: Context, attributeSet: AttributeSet?) :
    KavehColorSlider(context, attributeSet) {

    constructor(context: Context) : this(context, null)

    private var hsvHolder = FloatArray(3)

    private lateinit var hueBitmapShader: BitmapShader


    /**
     * Bitmap that is 360 pixels and each pixels represents a degree in hue.
     */
    private val hueBitmap: Bitmap = BitmapFactory.decodeResource(
        resources,
        R.drawable.full_hue_bitmap,
        BitmapFactory.Options().apply {
            inScaled = false
        })

    private val hueMatrix = Matrix()

    private var onHueChanged: ((hue: Float, argbColor: Int) -> Unit)? = null
    private var onHueChangedListener: OnHueChangedListener? = null
    private var onHueChangeEnd: ((hue: Float, argbColor: Int) -> Unit)? = null
    private var onHueChangeEndListener: OnHueChangeEndListener? = null

    var hue: Float = 30f
        set(value) {
            field = value
            isSliderChangingState = true
            circleXFactor = (value / 360f)
            calculateBounds(width.toFloat(), height.toFloat())
            invalidate()
        }
        get() =
            hsvHolder[0]

    override fun onCirclePositionChanged(circlePositionX: Float, circlePositionY: Float) {
        circleColor = calculateColorAt(circleX)

        callListeners(hsvHolder[0], circleColor)

        invalidate()
    }

    override fun onDragEnded(lastX: Float, lastY: Float) {
        callOnLastEventListener(hsvHolder[0], circleColor)
    }

    private fun calculateColorAt(ex: Float): Int {
        // Closer the indicator (handle) to the end of view the higher is hue value.
        hsvHolder[0] =
            floor(360f * (ex - drawingStart) / (widthF - drawingStart))

        // Brightness and saturation is left untouched.
        hsvHolder[1] = 1f
        hsvHolder[2] = 1f

        return Color.HSVToColor(hsvHolder)
    }

    override fun calculateBounds(targetWidth: Float, targetHeight: Float) {
        super.calculateBounds(targetWidth, targetHeight)
        circleColor = calculateColorAt(circleX)
    }

    override fun initializeSliderPaint() {
        hueBitmapShader =
            BitmapShader(hueBitmap, Shader.TileMode.MIRROR, Shader.TileMode.REPEAT).apply {
                setLocalMatrix(hueMatrix.apply {
                    // Resize the bitmap to match width of hue bitmap (360) to whatever width of view is.
                    setTranslate(drawingStart, 0f)

                    postScale(
                        (widthF - drawingStart) / hueBitmap.width,
                        1f,
                        drawingStart,
                        0f
                    )
                })
            }

        linePaint.shader = hueBitmapShader
    }

    fun setOnHueChangedListener(onHueChangedListener: OnHueChangedListener) {
        this.onHueChangedListener = onHueChangedListener
    }

    fun setOnHueChangedListener(onHueChangedListener: ((hue: Float, argbColor: Int) -> Unit)) {
        onHueChanged = onHueChangedListener
    }

    fun setOnHueChangeEndListener(listener: ((hue: Float, argbColor: Int) -> Unit)) {
        onHueChangeEnd = listener
    }

    fun setOnHueChangeEndListener(listener: OnHueChangeEndListener) {
        onHueChangeEndListener = listener
    }

    private fun callListeners(hue: Float, argbColor: Int) {
        onHueChanged?.invoke(hue, argbColor)
        onHueChangedListener?.onHueChanged(hue, argbColor)
    }

    private fun callOnLastEventListener(hue: Float, argbColor: Int) {
        onHueChangeEnd?.invoke(hue, argbColor)
        onHueChangeEndListener?.onHueChangeEnd(hue, argbColor)
    }

    interface OnHueChangedListener {
        fun onHueChanged(hue: Float, argbColor: Int)
    }

    interface OnHueChangeEndListener {
        fun onHueChangeEnd(hue: Float, argbColor: Int)
    }

}