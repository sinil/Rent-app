package com.riwal.rentalapp.common.extensions.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.ui.transition.BasicActivityTransition
import com.riwal.rentalapp.common.ui.transition.FadeActivityTransition
import com.riwal.rentalapp.common.ui.transition.PopActivityTransition
import com.riwal.rentalapp.common.ui.transition.PushActivityTransition
import org.joda.time.Duration
import kotlin.reflect.KClass

val Activity.contentView: ViewGroup
    get() = findViewById(android.R.id.content)

val Activity.statusBarBackgroundView: View?
    get() = findViewById(android.R.id.statusBarBackground)

val Activity.navigationBarBackgroundView: View?
    get() = findViewById(android.R.id.navigationBarBackground)

val Activity.displayMetrics: DisplayMetrics
    get() {
        val metrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(metrics)
        return metrics
    }

fun Activity.runOnUIThreadAfter(delay: Duration, action: () -> Unit) {
    Handler().postDelayed({
        runOnUiThread(action)
    }, delay.millis)
}

fun Activity.clearFocus() = currentFocus?.clearFocus()

fun Activity.clearFocusAndHideKeyboard() {
    clearFocus()
    hideKeyboard()
}

fun Activity.hideKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Activity.getString(@StringRes resId: Int?) = if (resId == null) null else getString(resId)

fun <T : Activity> Activity.defaultEnterTransitionForActivity(activityClass: KClass<T>): BasicActivityTransition {
    val activityInfo = activityInfoForActivity(activityClass)!!
    return if (activityInfo.theme == R.style.AppTheme_Dialog) FadeActivityTransition else PushActivityTransition
}

fun <T : Activity> Activity.defaultExitTransitionForActivity(activityClass: KClass<T>): BasicActivityTransition {
    val activityInfo = activityInfoForActivity(activityClass)!!
    return if (activityInfo.theme == R.style.AppTheme_Dialog) FadeActivityTransition else PopActivityTransition
}

fun <T : Activity> Activity.startActivity(activityClass: KClass<T>, flags: Int = 0, extras: Map<String, Any?> = emptyMap(), transition: BasicActivityTransition? = null) {
    val intent = Intent(this, activityClass.java).putExtras(extras)
    intent.flags = flags
    startActivity(intent)
    overridePendingTransition(transition ?: defaultEnterTransitionForActivity(activityClass))
}

fun Activity.startActivity(intent: Intent, sharedElement: View, sharedElementName: String, sharedStatusBar: Boolean = true, sharedNavigationBar: Boolean = true) {
    startActivity(intent, listOf(Pair(sharedElement, sharedElementName)), sharedStatusBar, sharedNavigationBar)
}

fun Activity.startActivity(intent: Intent, sharedElements: List<Pair<View, String>>, sharedStatusBar: Boolean = true, sharedNavigationBar: Boolean = true) {

    val shared = sharedElements.toMutableList()

    if (sharedStatusBar && statusBarBackgroundView != null) {
        shared.add(Pair(statusBarBackgroundView!!, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME))
    }

    if (sharedNavigationBar && navigationBarBackgroundView != null) {
        shared.add(Pair(navigationBarBackgroundView!!, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME))
    }

    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *shared.toTypedArray())
    startActivity(intent, options.toBundle())
}

fun Activity.overridePendingTransition(transition: BasicActivityTransition) = overridePendingTransition(transition.enterAnimationRes, transition.exitAnimationRes)

fun Activity.finishWithResult(resultCode: Int = Activity.RESULT_OK, extras: Map<String, Any?> = emptyMap()) {
    setResult(resultCode, Intent().putExtras(extras))
    finish()
}

fun Activity.beginDelayedTransition(transition: Transition = AutoTransition()) = TransitionManager.beginDelayedTransition(contentView, transition)

fun Activity.onUiThread(action: () -> Unit) {
    if (isFinishing) {
        return
    }
    runOnUiThread(action)
}

fun Activity.dp(value: Float) = resources.dp(value)
fun Activity.dp(value: Int) = resources.dp(value)
fun Activity.dimen(@DimenRes resId: Int) = resources.getDimensionPixelSize(resId)
fun Activity.color(@ColorRes resId: Int) = resources.getColor(resId)
fun Activity.colorStateList(@ColorRes resId: Int) = resources.getColorStateList(resId)