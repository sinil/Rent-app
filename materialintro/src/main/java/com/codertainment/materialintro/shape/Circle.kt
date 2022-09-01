package com.codertainment.materialintro.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import com.codertainment.materialintro.target.Target
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class Circle(target: Target, focus: Focus, focusGravity: FocusGravity, padding: Int) :
  Shape(target, focus, focusGravity, padding) {

  private var radius = 0
  private var circlePoint: Point
  override fun draw(canvas: Canvas, eraser: Paint, padding: Int) {
    calculateRadius(padding)
    circlePoint = focusPoint
    canvas.drawCircle(circlePoint.x.toFloat(), circlePoint.y.toFloat(), radius.toFloat(), eraser)
  }

  override fun reCalculateAll() {
    calculateRadius(padding)
    circlePoint = focusPoint
  }

  private fun calculateRadius(padding: Int) {
    val side: Int
    side = when (focus) {
      Focus.MINIMUM -> min(target.rect.width() / 2, target.rect.height() / 2)
      Focus.ALL -> max(
        target.rect.width() / 2,
        target.rect.height() / 2
      )
      else -> {
        val minSide = min(target.rect.width() / 2, target.rect.height() / 2)
        val maxSide = max(target.rect.width() / 2, target.rect.height() / 2)
        (minSide + maxSide) / 2
      }
    }
    radius = side + padding
  }

  override val point: Point
    get() = circlePoint

  override val height: Int
    get() = 2 * radius

  override fun isTouchOnFocus(x: Double, y: Double): Boolean {
    val xV = point.x
    val yV = point.y
    val dx = (x - xV).pow(2.0)
    val dy = (y - yV).pow(2.0)
    return dx + dy <= radius.toDouble().pow(2.0)
  }

  init {
    circlePoint = focusPoint
    calculateRadius(padding)
  }
}