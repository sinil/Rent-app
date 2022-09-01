package com.riwal.rentalapp.common.extensions.animation

import android.view.View
import androidx.transition.Transition
import androidx.transition.TransitionSet

val TransitionSet.transitions
    get() = (0 until transitionCount).map { getTransitionAt(it) }

fun Transition.excludeTargets(excludedTargets: List<View>, exclude: Boolean = true) = excludedTargets.foldRight(this) { target, transition -> transition.excludeTarget(target, exclude) }