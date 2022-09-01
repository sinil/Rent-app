package com.codertainment.materialintro.target

import android.graphics.Point
import android.graphics.Rect
import android.view.View

class ViewTarget(override val view: View) : Target {
  override val point: Point
    get() {
      val location = IntArray(2)
      view.getLocationInWindow(location)
      return Point(location[0] + view.width / 2, location[1] + view.height / 2)
    }

  override val rect: Rect
    get() {
      val location = IntArray(2)
      view.getLocationInWindow(location)
      return Rect(
        location[0],
        location[1],
        location[0] + view.width,
        location[1] + view.height
      )
    }

}