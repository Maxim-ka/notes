package com.reschikov.geekbrains.notes.view.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.annotation.Dimension.PX
import androidx.core.content.ContextCompat
import com.reschikov.geekbrains.notes.R
import org.jetbrains.anko.dip

@Dimension(unit = DP) private const val RADIUS_DP = 16
@Dimension(unit = DP) private const val STROKE_WIDTH_DP = 1

class ColorCircleView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private var center: Pair<Float, Float> = 0.0f to 0.0f

    @Dimension(unit = PX)
    var radius: Float = dip(RADIUS_DP).toFloat()

    @ColorRes
    var fillColorRes: Int = R.color.color_white
        set (value) {
            field = value
            fillPaint.color = ContextCompat.getColor(context, value)
        }

    @ColorRes
    var strokeColorRes: Int = R.color.design_default_color_on_secondary
        set (value) {
            field = value
            strokePaint.color = ContextCompat.getColor(context, value)
        }

    @Dimension(unit = PX)
    var strokeWidth: Float = dip(STROKE_WIDTH_DP).toFloat()
        set (value) {
            field = value
            strokePaint.strokeWidth = value
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorCircleView)
        val defRadiusPx = dip(RADIUS_DP).toFloat()
        radius = typedArray.getDimension(R.styleable.ColorCircleView_circleRadius, defRadiusPx)
        fillColorRes = typedArray.getResourceId(R.styleable.ColorCircleView_fillColor, R.color.color_white)
        val defStrokeWidthPx = dip(STROKE_WIDTH_DP).toFloat()
        strokeWidth = typedArray.getDimension(R.styleable.ColorCircleView_strokeWidth, defStrokeWidthPx)
        strokeColorRes = typedArray.getResourceId(R.styleable.ColorCircleView_strokeColor,
                R.color.design_default_color_on_secondary)
        typedArray.recycle()
    }

    override fun onMeasure (widthMeasureSpec: Int , heightMeasureSpec: Int ) {
        val height = (radius * 2 + paddingTop + paddingBottom).toInt()
        val width = (radius * 2 + paddingStart + paddingEnd).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onLayout (changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        center = measuredWidth / 2.0f to measuredHeight / 2.0f
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw (canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(center.first, center.second, radius, strokePaint)
        canvas.drawCircle(center.first, center.second, radius - strokeWidth, fillPaint)
    }
}