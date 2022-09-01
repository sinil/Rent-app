package com.riwal.rentalapp.common.extensions.android

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.util.SizeF
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.util.Pair
import androidx.fragment.app.FragmentActivity
import androidx.transition.Transition
import com.riwal.rentalapp.common.Vector2
import com.riwal.rentalapp.common.mvc.BaseActivity
import com.riwal.rentalapp.common.ui.transition.BasicActivityTransition
import com.riwal.rentalapp.common.ui.transition.PushActivityTransition
import io.reactivex.Single
import kotlin.math.hypot
import kotlin.reflect.KClass

var View.isVisible
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }

val View.supportFragmentManager
    get() = (context.activity as FragmentActivity).supportFragmentManager!!

val View.activity
    get() = context.activity!!

val View.window
    get() = activity.window

val View.displayMetrics
    get() = activity.displayMetrics

var View.backgroundColor: Int?
    @ColorInt get() = (background as? ColorDrawable)?.color
    set(value) {
        background = if (value == null) null else ColorDrawable(value)
    }

val View.mostDistantFocusableAncestor
    get() = ancestors.lastOrNull { it.isFocusableInTouchMode }

var View.horizontalBias
    get() = (layoutParams as ConstraintLayout.LayoutParams).horizontalBias
    set(value) {
        updateConstraints {
            it.setHorizontalBias(this.id, value)
        }
    }

fun View.updateConstraints(updateBlock: (ConstraintSet) -> Unit) {
    val constraints = ConstraintSet()
    constraints.clone(constraintLayout)
    updateBlock(constraints)
    constraints.applyTo(constraintLayout)
}

val View.constraintLayout
    get() = ancestors.filterIsInstance<ConstraintLayout>().firstOrNull()

val View.ancestors: List<View>
    get() {
        val parent = parent as? View ?: return emptyList()
        return listOf(parent) + parent.ancestors
    }

var View.center
    get() = PointF(x + width.toFloat() / 2, y + height.toFloat() / 2)
    set(value) {
        x = value.x - width.toFloat() / 2
        y = value.y - height.toFloat() / 2
    }

var View.scale
    get() = Vector2(x = scaleX, y = scaleY)
    set(value) {
        scaleX = value.x
        scaleY = value.y
    }

val View.centerRelative
    get() = PointF(width.toFloat() / 2, height.toFloat() / 2)

var View.position
    get() = PointF(x, y)
    set(pos) {
        marginLeft = pos.x.toInt()
        marginTop = pos.y.toInt()
    }

val View.boundingCircleRadius
    get() = hypot(width.toDouble() / 2, height.toDouble() / 2).toFloat()

val View.boundingCircleDiameter
    get() = boundingCircleRadius * 2

var View.layoutHeight
    get() = layoutParams.height
    set(value) {
        val params = layoutParams
        params.height = value
        layoutParams = params
    }

var View.layoutWidth
    get() = layoutParams.width
    set(value) {
        val params = layoutParams
        params.width = value
        layoutParams = params
    }

val View.bounds
    get() = RectF(origin = PointF(0f, 0f), size = size)

val View.size
    get() = SizeF(width.toFloat(), height.toFloat())

fun RectF(origin: PointF, size: SizeF) = RectF(origin.x, origin.y, size.width, size.height)

fun RectF.insetBy(dx: Float, dy: Float) = RectF(left + dx, top + dy, right - dx, bottom - dy)

fun RectF.insetBy(insets: RectF) = RectF(left + insets.left, top + insets.top, right - insets.right, bottom - insets.bottom)

var View.marginParams: ViewGroup.MarginLayoutParams?
    get() = layoutParams as? ViewGroup.MarginLayoutParams
    set(value) {
        layoutParams = value
    }

var View.marginLeft
    get() = marginParams!!.leftMargin
    set(value) {
        val params = marginParams!!
        params.leftMargin = value
        layoutParams = params
    }

var View.marginTop
    get() = (layoutParams as ViewGroup.MarginLayoutParams).topMargin
    set(value) {
        val params = marginParams!!
        params.topMargin = value
        layoutParams = params
    }

var View.marginRight
    get() = (layoutParams as ViewGroup.MarginLayoutParams).rightMargin
    set(value) {
        val params = marginParams!!
        params.rightMargin = value
        layoutParams = params
    }

var View.marginBottom
    get() = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    set(value) {
        val params = marginParams!!
        params.bottomMargin = value
        layoutParams = params
    }

fun View.startActivity(intent: Intent) = context.startActivity(intent)
fun View.startActivity(intent: Intent, sharedElement: View, sharedElementName: String, sharedStatusBar: Boolean = true, sharedNavigationBar: Boolean = true) = activity.startActivity(intent, listOf(Pair(sharedElement, sharedElementName)), sharedStatusBar, sharedNavigationBar)

fun <T : Activity> View.startActivity(activityClass: KClass<T>, extras: Map<String, Any> = emptyMap(), transition: BasicActivityTransition = PushActivityTransition) = activity.startActivity(activityClass, extras = extras, transition = transition)
fun <T : Activity> View.startActivityForResult(activityClass: KClass<T>, extras: Map<String, Any?> = emptyMap(), transition: BasicActivityTransition = PushActivityTransition): Single<Intent> {
    return (activity as? BaseActivity)?.startActivityForResult(activityClass, extras, transition) ?: Single.error(IllegalStateException("Cannot start activity for result, calling activity is not a BaseActivity"))
}

fun View.requestPermission(permission: String) = (activity as? BaseActivity)?.requestPermission(permission)
        ?: Single.error(IllegalStateException("Cannot ask for permission, calling activity is not a BaseActivity"))

fun View.hasPermission(permission: String) = activity.hasPermission(permission)

fun View.runOnUiThread(action: () -> Unit) {
    post(action)
}

fun View.getString(@StringRes resId: Int?, vararg parameters: Any?) = if (resId == null) null else resources.getString(resId, *parameters)
fun View.dp(value: Float): Float = resources.dp(value)
fun View.dp(value: Int): Int = resources.dp(value)
fun View.dimen(@DimenRes resId: Int) = resources.getDimensionPixelOffset(resId)
fun View.color(@ColorRes resId: Int) = resources.getColor(resId)
fun View.colorStateList(@ColorRes resId: Int) = resources.getColorStateList(resId)

fun View.requestFocusAndShowKeyboard() {
    requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.clearFocusAndHideKeyboard() {
    clearFocus()
    mostDistantFocusableAncestor?.requestFocus()
    hideKeyboard()
}

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.circularReveal(center: PointF = this.center, startRadius: Float = 0.0f, endRadius: Float = boundingCircleRadius): Animator {
    val animator = ViewAnimationUtils.createCircularReveal(this, center.x.toInt(), center.y.toInt(), startRadius, endRadius)
    animator.interpolator = AccelerateDecelerateInterpolator()
    return animator
}

fun View.circularHide(center: PointF = this.center, startRadius: Float = boundingCircleRadius, endRadius: Float = 0.0f): Animator {
    val animator = ViewAnimationUtils.createCircularReveal(this, center.x.toInt(), center.y.toInt(), startRadius, endRadius)
    animator.interpolator = AccelerateDecelerateInterpolator()
    return animator
}

fun View.setPaddingStart(value: Int) = setPaddingRelative(value, paddingTop, paddingEnd, paddingBottom)
fun View.setPaddingEnd(value: Int) = setPaddingRelative(paddingStart, paddingTop, value, paddingBottom)

fun View.setPaddingLeft(value: Int) = setPadding(value, paddingTop, paddingRight, paddingBottom)
fun View.setPaddingTop(value: Int) = setPadding(paddingLeft, value, paddingRight, paddingBottom)
fun View.setPaddingRight(value: Int) = setPadding(paddingLeft, paddingTop, value, paddingBottom)
fun View.setPaddingBottom(value: Int) = setPadding(paddingLeft, paddingTop, paddingRight, value)

fun View.removeFromParent() = (parent as ViewGroup).removeView(this)

// TODO: Better name
fun ViewPropertyAnimator.makeNormal() = alpha(1.0f).scaleX(1.0f).scaleY(1.0f)!!

fun View.makeNormal() {
    alpha = 1.0f
    scaleX = 1.0f
    scaleY = 1.0f
}

fun Transition.onTransitionStart(handler: () -> Unit): Transition {
    addListener(object : TransitionListenerAdapter() {
        override fun onTransitionStart(transition: Transition) {
            handler()
        }
    })
    return this
}

fun Transition.onTransitionEnd(handler: () -> Unit): Transition {

    addListener(object : TransitionListenerAdapter() {

        override fun onTransitionEnd(transition: Transition) {
            handler()
        }

        override fun onTransitionCancel(transition: Transition) {
            handler()
        }
    })
    return this
}

open class TransitionListenerAdapter : Transition.TransitionListener {
    override fun onTransitionEnd(transition: Transition) {}
    override fun onTransitionResume(transition: Transition) {}
    override fun onTransitionPause(transition: Transition) {}
    override fun onTransitionCancel(transition: Transition) {}
    override fun onTransitionStart(transition: Transition) {}
}
