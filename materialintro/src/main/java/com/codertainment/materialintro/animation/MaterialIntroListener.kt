package com.codertainment.materialintro.animation

interface MaterialIntroListener {
  /**
   * @param onUserClick is true when MIV has been dismissed through user click, false when MIV was previously displayed and was set as saved
   * @param viewId Unique ID of the target view
   */
  fun onIntroDone(onUserClick: Boolean, viewId: String)
}