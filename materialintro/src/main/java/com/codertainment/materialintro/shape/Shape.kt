package com.codertainment.materialintro.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import com.codertainment.materialintro.target.Target
import com.codertainment.materialintro.utils.Constants

abstract class Shape @JvmOverloads constructor(
  protected var target: Target,
  protected var focus: Focus = Focus.MINIMUM,
  private val focusGravity: FocusGravity = FocusGravity.CENTER,
  protected var padding: Int = Constants.DEFAULT_TARGET_PADDING
) {

  abstract fun draw(canvas: Canvas, eraser: Paint, padding: Int)
  protected val focusPoint get() = when {
    focusGravity === FocusGravity.LEFT -> {
      val xLeft = target.rect.left + (target.point.x - target.rect.left) / 2
      Point(xLeft, target.point.y)
    }
    focusGravity === FocusGravity.RIGHT -> {
      val xRight = target.point.x + (target.rect.right - target.point.x) / 2
      Point(xRight, target.point.y)
    }
    else -> target.point
  }

  abstract fun reCalculateAll()
  abstract val point: Point
  abstract val height: Int

  /**
   * Determines if a click is on the shape
   * @param x x-axis location of click
   * @param y y-axis location of click
   * @return true if click is inside shape
   */
  abstract fun isTouchOnFocus(x: Double, y: Double): Boolean
}