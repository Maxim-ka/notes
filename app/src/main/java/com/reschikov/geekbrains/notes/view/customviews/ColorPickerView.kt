package com.reschikov.geekbrains.notes.view.customviews

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import com.reschikov.geekbrains.notes.getResourceColor
import com.reschikov.geekbrains.notes.repository.model.ColorNote
import org.jetbrains.anko.dip

private const val PALETTE_ANIMATION_DURATION = 1_500L
private const val FACTOR = 0.5f
private const val HEIGHT = "height"
private const val SCALE = "scale"
@Dimension(unit = DP) private const val COLOR_VIEW_PADDING = 16

class ColorPickerView : LinearLayout {

    var onColorClickListener: (color: ColorNote) -> Unit = { }
    val isOpen: Boolean
        get () = measuredHeight > 0
    private var desiredHeight = WRAP_CONTENT
    private val animator by lazy {
        ValueAnimator().apply {
            duration = PALETTE_ANIMATION_DURATION
            interpolator = DecelerateInterpolator(FACTOR)
            addUpdateListener(updateListener)
        }
    }
    private val updateListener by lazy {
        ValueAnimator.AnimatorUpdateListener { animator ->
            layoutParams.apply {
                height = animator.getAnimatedValue(HEIGHT) as Int
            }.let {
                layoutParams = it
            }
            val scaleFactor = animator.getAnimatedValue(SCALE) as Float
            for (i in 0 until childCount) {
                getChildAt(i).apply {
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    alpha = scaleFactor
                }
            }
        }
    }

    constructor (context: Context) : this(context, null , 0)
    constructor (context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super (context, attrs, defStyleAttr) {
        ColorNote.values().forEach { color ->
            addView(ColorCircleView(context).apply {
                fillColorRes = color.getResourceColor()
                tag = color
                dip(COLOR_VIEW_PADDING).let {
                    setPadding(it, it, it, it)
                }
                setOnClickListener { onColorClickListener(it.tag as ColorNote) }
            })
        }
    }

    fun open() {
        animator.cancel()
        animator.setValues(PropertyValuesHolder.ofInt(HEIGHT, measuredHeight, desiredHeight),
                PropertyValuesHolder.ofFloat(SCALE, getChildAt(0).scaleX, 1.0f))
        animator.start()
    }

    fun close() {
        animator.cancel()
        animator.setValues(PropertyValuesHolder.ofInt(HEIGHT, measuredHeight, 0),
                PropertyValuesHolder.ofFloat(SCALE, getChildAt(0).scaleX, 0.0f))
        animator.start()
    }
}