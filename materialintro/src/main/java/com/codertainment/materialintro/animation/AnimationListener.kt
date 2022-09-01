package com.codertainment.materialintro.animation

interface AnimationListener {

  /**
   * We need to make MaterialIntroView visible
   * before fade in animation starts
   */
  interface OnAnimationStartListener {

    fun onAnimationStart()
  }

  /**
   * We need to make MaterialIntroView invisible
   * after fade out animation ends.
   */
  interface OnAnimationEndListener {

    fun onAnimationEnd()
  }
}