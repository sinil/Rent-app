package com.riwal.rentalapp.common.ui

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

class GestureDetector(context: Context) : View.OnTouchListener, GestureDetector.OnGestureListener {

    private val gestureDetector = GestureDetectorCompat(context, this)

    private var panListener: ((event: MotionEvent) -> Boolean)? = null

    override fun onTouch(v: View, event: MotionEvent) = gestureDetector.onTouchEvent(event)

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?) = false
    override fun onDown(e: MotionEvent?) = false
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float) = false
    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float) = panListener?.invoke(e2) ?: false
    override fun onLongPress(e: MotionEvent?) {

    }

    fun setOnPanListener(listener: (event: MotionEvent) -> Boolean) {
        panListener = listener
    }
}