package com.riwal.rentalapp.common.extensions.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.PointF
import android.util.Property
import android.view.View
import android.view.ViewPropertyAnimator
import com.riwal.rentalapp.common.Vector2
import com.riwal.rentalapp.common.extensions.android.center
import com.riwal.rentalapp.common.extensions.android.scale
import com.riwal.rentalapp.common.lerp
import org.joda.time.Duration

fun ViewPropertyAnimator.duration(value: Long): ViewPropertyAnimator {
    duration = value
    return this
}

fun ViewPropertyAnimator.moveTo(point: PointF): ViewPropertyAnimator {
    return x(point.x).y(point.y)
}

fun ViewPropertyAnimator.onEnd(handler: () -> Unit): ViewPropertyAnimator {
    return setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            handler()
        }

        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
    })
}

fun Animator.onStart(handler: () -> Unit): Animator {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            super.onAnimationStart(animation)
            handler()
        }
    })
    return this
}

fun Animator.onEnd(handler: () -> Unit): Animator {
    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            handler()
        }
    })
    return this
}

fun Animator.setDuration(duration: Duration) {
    this.duration = duration.millis
}

fun Animator.setStartDelay(delay: Duration) {
    this.startDelay = delay.millis
}

object Vector2Interpolator : TypeEvaluator<Vector2> {
    override fun evaluate(fraction: Float, startValue: Vector2, endValue: Vector2) = lerp(startValue, endValue, fraction)
}

class IdleAnimator : ValueAnimator() {

    init {
        setFloatValues(0f, 1f)
    }

}

object ViewProperties {

    val CENTER = object : Property<View, PointF>(PointF::class.java, "center") {

        override fun get(view: View) = view.center

        override fun set(view: View, center: PointF) {
            view.center = center
        }
    }

    val SCALE = object : Property<View, Vector2>(Vector2::class.java, "scale") {

        override fun get(view: View) = view.scale

        override fun set(view: View, scale: Vector2) {
            view.scale = scale
        }
    }

}