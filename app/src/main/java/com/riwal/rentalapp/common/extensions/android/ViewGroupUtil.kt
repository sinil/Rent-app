package com.riwal.rentalapp.common.extensions.android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager.beginDelayedTransition

fun ViewGroup.beginDelayedTransition(transition: Transition = AutoTransition().onTransitionStart { }) {
    beginDelayedTransition(this, transition)
}

val ViewGroup.layoutInflater
    get() = LayoutInflater.from(context)

fun <T : View> ViewGroup.inflate(@LayoutRes layoutRes: Int, addAsSubview: Boolean = false) = layoutInflater.inflate(layoutRes, this, addAsSubview) as T

fun ViewGroup.addSubview(@LayoutRes layoutRes: Int) = inflate<View>(layoutRes, addAsSubview = true)

val ViewGroup.children
    get() = (0 until childCount).map { getChildAt(it) }