package com.reschikov.geekbrains.notes.view.utilities

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomappbar.BottomAppBar
import kotlinx.android.synthetic.main.activity_main.view.*


class BehaviorFABAndBottomAppBar(context: Context, attrs: AttributeSet) :
        HideBottomViewOnScrollBehavior<View>(context, attrs) {

    private var isDown: Boolean = false
    private lateinit var bottomAppBar: BottomAppBar

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        bottomAppBar = parent.bottomAppBar
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun slideUp(child: View) {
        super.slideUp(child)
        isDown = false
    }

    override fun slideDown(child: View) {
        super.slideDown(child)
        isDown = true
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout,
                                    child: View,
                                    target: View, type: Int) {
        if (isDown) {
            toLift(child, target)
        } else {
            toBringBack(target)
        }
    }

    private fun toLift(child: View, target: View) {
        val layoutParams = target.layoutParams as CoordinatorLayout.LayoutParams
        val heightBottomAppBar = bottomAppBar.height
        bottomAppBar.performShow()
        slideUp(child)
        layoutParams.bottomMargin = child.height / 2 + heightBottomAppBar
        target.layoutParams = layoutParams
    }

    private fun toBringBack(target: View) {
        val layoutParams = target.layoutParams as CoordinatorLayout.LayoutParams
        if (layoutParams.bottomMargin == 0) return
        layoutParams.bottomMargin = 0
        target.layoutParams = layoutParams
    }
}