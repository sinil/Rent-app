package com.codertainment.materialintro.target

import android.graphics.Point
import android.graphics.Rect
import android.view.View

interface Target {
  /**
   * Returns center point of target. We can get x and y coordinates using point object
   */
  val point: Point

  /**
   * Returns Rectangle points of target view
   */
  val rect: Rect

  /**
   * return target view
   */
  val view: View
}