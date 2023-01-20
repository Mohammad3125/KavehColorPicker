package ir.kotlin.kavehcolorpicker

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import kotlin.math.max
import kotlin.math.min

class KavehColorPicker(context: Context, attributeSet: AttributeSet?) :
    KavehColorSlider(context, attributeSet) {

    constructor(context: Context) : this(context, null)

    private lateinit var colorShader: LinearGradient
    private lateinit var darknessShader: LinearGradient

    private val hsvArray = FloatArray(3)

    /**
     * Hue value in color picker. Should be in range of 0 to 360.
     */
    var hue = 30
        set(value) {
            if (value < 0f || value > 360f) {
                throw IllegalStateException("hue value should be between 0 and 360")
            }

            field = value

            initializeSliderPaint()

            calculateColor(circleX, circleY)

            invalidate()
        }

    var circleIndicatorRadius = dp(12)
        set(value) {
            field = value
            invalidate()
        }

    var alphaSliderView: KavehColorAlphaSlider? = null
        set(value) {
            field = value

            alphaSliderView?.let { alphaSlider ->
                alphaSlider.selectedColor = colorWithFullAlpha

                alphaSlider.setOnAlphaChangedListener {
                    alphaValue = (255 * it).toInt()
                    callListeners()
                }
            }
        }

    var hueSliderView: KavehHueSlider? = null
        set(value) {
            if (value != null) {
                field = value

                this.hue = value.hue.toInt()

                value.setOnHueChangedListener { hue, argbColor ->
                    this.hue = hue.toInt()
                }
            }
        }


    private var colorWithFullAlpha = Color.RED

    /**
     * This value represents selected color in color picker.
     * If [KavehColorAlphaSlider] is connected to this view via [alphaSliderView] then alpha value is
     * taken from [alphaSliderView] and applied on the final color.
     */
    var color = Color.TRANSPARENT
        private set
        get() {
            return Color.HSVToColor(alphaValue, hsvArray)
        }


    private var alphaValue = 255

    private var onColorChanged: ((color: Int) -> Unit)? = null
    private var onColorChangedListener: OnColorChangedListener? = null

    private var defaultSize = dp(320).toInt()

    init {
        linePaint.style = Paint.Style.FILL
    }

    override fun onCirclePositionChanged(circlePositionX: Float, circlePositionY: Float) {
        calculateColor(circlePositionX, circlePositionY)

        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val finalWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                measureWidth
            }
            MeasureSpec.AT_MOST -> {
                min(defaultSize, measureWidth)
            }
            MeasureSpec.UNSPECIFIED -> {
                defaultSize
            }
            else -> {
                suggestedMinimumWidth
            }
        }

        val finalHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                measureHeight
            }
            MeasureSpec.AT_MOST -> {
                min(defaultSize, measureHeight)
            }
            MeasureSpec.UNSPECIFIED -> {
                defaultSize
            }
            else -> {
                suggestedMinimumWidth
            }
        }

        setMeasuredDimension(
            max(finalWidth, suggestedMinimumWidth),
            max(finalHeight, suggestedMinimumHeight)
        )
    }


    override fun calculateBounds(targetWidth: Float, targetHeight: Float) {
        val fx = (circleX - drawingStart) / (widthF - drawingStart)
        val fy = (circleY - drawingTop) / (heightF - drawingTop)

        widthF = targetWidth - paddingEnd - circleIndicatorRadius
        heightF = targetHeight - paddingBottom - circleIndicatorRadius

        drawingStart = paddingStart + circleIndicatorRadius
        drawingTop = paddingTop + circleIndicatorRadius

        if (isFirstTimeLaying) {
            isFirstTimeLaying = false
            circleX = widthF
            circleY = drawingTop
        } else if (isRestoredState) {
            circleX = ((widthF - drawingStart) * circleXFactor) + drawingStart
            circleY = ((heightF - drawingTop) * circleYFactor) + drawingTop

            circleXFactor = 0f
            circleYFactor = 0f

            isRestoredState = false
        } else {
            circleX = ((widthF - drawingStart) * fx) + drawingStart
            circleY = ((heightF - drawingTop) * fy) + drawingTop
        }
    }

    private fun calculateColor(ex: Float, ey: Float) {
        hsvArray[0] = hue.toFloat()
        if (!isFirstTimeLaying) {
            hsvArray[1] = (ex - drawingStart) / (widthF - drawingStart)
            hsvArray[2] = 1f - ((ey - drawingTop) / (heightF - drawingTop))
        } else {
            hsvArray[1] = 1f
            hsvArray[2] = 1f
        }

        colorWithFullAlpha = Color.HSVToColor(hsvArray)

        alphaSliderView?.selectedColor = colorWithFullAlpha

        callListeners()
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        canvas.drawRect(drawingStart, drawingTop, widthF, heightF, linePaint.apply {
            shader = colorShader
        })

        canvas.drawRect(drawingStart, drawingTop, widthF, heightF, linePaint.apply {
            shader = darknessShader
        })

        canvas.drawCircle(
            circleX,
            circleY,
            circleIndicatorRadius,
            circlePaint.apply {
                color = strokeColor
            })

        canvas.drawCircle(
            circleX,
            circleY,
            circleIndicatorRadius - strokeSize,
            circlePaint.apply {
                color = colorWithFullAlpha
            })
    }


    override fun initializeSliderPaint() {
        hsvArray[0] = hue.toFloat()
        hsvArray[1] = 1f
        hsvArray[2] = 1f

        colorShader =
            LinearGradient(
                drawingStart,
                0f,
                widthF,
                0f,
                Color.WHITE,
                Color.HSVToColor(hsvArray),
                Shader.TileMode.MIRROR
            )

        darknessShader =
            LinearGradient(
                0f,
                drawingTop,
                0f,
                heightF,
                Color.TRANSPARENT,
                Color.BLACK,
                Shader.TileMode.MIRROR
            )

        calculateColor(circleX, circleY)
    }

    override fun onSaveInstanceState(): Parcelable {
        return (super.onSaveInstanceState() as Bundle).apply {
            putInt(HUE_KEY, hue)
            putInt(ALPHA_KEY, alphaValue)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as Bundle).let { bundle ->
            isFirstTimeLaying = false
            alphaValue = bundle.getInt(ALPHA_KEY)
            hue = bundle.getInt(HUE_KEY)
        }
        super.onRestoreInstanceState(state)
    }

    fun setOnColorChangedListener(onColorChangedListener: OnColorChangedListener) {
        this.onColorChangedListener = onColorChangedListener
        callListeners()
    }

    fun setOnColorChangedListener(onColorChangedListener: ((color: Int) -> Unit)) {
        onColorChanged = onColorChangedListener
        callListeners()
    }

    private fun callListeners() {
        val color = Color.HSVToColor(alphaValue, hsvArray)
        onColorChanged?.invoke(color)
        onColorChangedListener?.onColorChanged(color)
    }


    interface OnColorChangedListener {
        fun onColorChanged(color: Int)
    }

    companion object {
        private const val HUE_KEY = "hue"
        private const val ALPHA_KEY = "alpha"
    }


}