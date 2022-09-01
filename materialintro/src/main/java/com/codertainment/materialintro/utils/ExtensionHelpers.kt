package com.codertainment.materialintro.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.codertainment.materialintro.MaterialIntroConfiguration
import com.codertainment.materialintro.animation.MaterialIntroListener
import com.codertainment.materialintro.prefs.PreferencesManager
import com.codertainment.materialintro.sequence.MaterialIntroSequence
import com.codertainment.materialintro.sequence.MaterialIntroSequenceListener
import com.codertainment.materialintro.view.MaterialIntroView

/**
 * Create an instance of MaterialIntroView with the passed config or properties applied in the lambda
 *
 * If your Activity implements MaterialIntroListener, it is automatically assigned as materialIntroListener for the current created instance
 *
 * @param config (Optional) The MaterialIntroConfiguration to be used. For e.g. you can set a global config for your app and override the targetView and/or
 * other properties in the lambda function
 *
 * @param show Indicates whether this instance of MaterialIntroView should show instantly after instantiation and initialisation
 */
fun Activity.materialIntro(show: Boolean = false, config: MaterialIntroConfiguration? = null, func: MaterialIntroView.() -> Unit): MaterialIntroView =
        MaterialIntroView(this).apply {
            if (this@materialIntro is MaterialIntroListener) {
                materialIntroListener = this@materialIntro
            }
            withConfig(config)
            func()
            if (show) {
                show(this@materialIntro)
            }
        }


fun FrameLayout.materialIntro(activity: Activity?, targetView: View? = null, show: Boolean = false, config: MaterialIntroConfiguration? = null, func: MaterialIntroView.() -> Unit): MaterialIntroView? {
    return if (activity == null) null
    else MaterialIntroView(activity).apply {
        if (activity is MaterialIntroListener) {
            materialIntroListener = activity as MaterialIntroListener
        }
        if (this@materialIntro is MaterialIntroListener) {
            materialIntroListener = this@materialIntro
        }
        withConfig(config)
        func()
        if (show) {
            show(activity)
        }
    }
}

/**
 * Create an instance of MaterialIntroView for a Fragment's activity with the passed config or properties applied in the lambda
 *
 * If your Activity/Fragment implements MaterialIntroListener, it is automatically assigned as materialIntroListener for the current created instance

 * @param config (Optional) The MaterialIntroConfiguration to be used. For e.g. you can set a global config for your app and override the targetView and/or
 * other properties in the lambda function
 *
 * @param show Indicates whether this instance of MaterialIntroView should show instantly after instantiation and initialisation
 */
fun Fragment.materialIntro(show: Boolean = false, config: MaterialIntroConfiguration? = null, func: MaterialIntroView.() -> Unit): MaterialIntroView? {
    return if (activity == null) null
    else MaterialIntroView(activity!!).apply {
        if (activity is MaterialIntroListener) {
            materialIntroListener = activity as MaterialIntroListener
        }
        if (this@materialIntro is MaterialIntroListener) {
            materialIntroListener = this@materialIntro
        }
        withConfig(config)
        func()
        if (show) {
            show(activity!!)
        }
    }
}

/**
 * Reset supplied viewId's displayed state
 *
 * @param viewId the viewId who's displayed state needs to be reset
 */
fun Context.resetMivDisplayed(viewId: String?) {
    preferencesManager.reset(viewId)
}

/**
 * Reset all saved displayed states
 */
fun Context.resetAllMivs() {
    preferencesManager.resetAll()
}

/**
 * Reset all saved displayed states
 */
fun Context.skipTutorial() {
    preferencesManager.skipTutorial()
}

internal val Context.preferencesManager
    get() = PreferencesManager.getInstance(this)


val Activity.materialIntroSequence
    get() = MaterialIntroSequence.getInstance(this)

/**
 * Create/get MaterialIntroSequence for the current activity
 *
 * If your Activity implements MaterialIntroSequenceListener, it is automatically assigned as materialIntroSequenceListener for the current created instance
 *
 * @param initialDelay delay for the first MIV to be shown
 *
 * @param materialIntroSequenceListener listener for MaterialIntroSequence events
 *
 * @param showSkip Whether to show the skip button for MIVs
 *
 * @param persistSkip If enabled, once the user clicks on skip button, all new MIVs will be skipped too, else even after the user clicks on skip
 * button and new MIVs are added after that, for e.g. for another fragment, the new MIVs will be shown
 */
fun Activity.materialIntroSequence(
        initialDelay: Long? = null, materialIntroSequenceListener: MaterialIntroSequenceListener? = null, showSkip: Boolean? = null, persistSkip: Boolean? = null,
        func: MaterialIntroSequence.() -> Unit
): MaterialIntroSequence {
    return materialIntroSequence.apply {
        if (this@materialIntroSequence is MaterialIntroSequenceListener) {
            this.materialIntroSequenceListener = this@materialIntroSequence
        }
        showSkip?.let {
            this.showSkip = it
        }
        persistSkip?.let {
            this.persistSkip = it
        }
        initialDelay?.let {
            this.initialDelay = it
        }
        materialIntroSequenceListener?.let {
            this.materialIntroSequenceListener = it
        }
        func()
        start()
    }
}

val Fragment.materialIntroSequence
    get() = if (activity != null) MaterialIntroSequence.getInstance(activity!!) else null

/**
 * Create/get MaterialIntroSequence for the current fragment's activity
 *
 * If your Activity/Fragment implements MaterialIntroSequenceListener, it is automatically assigned as materialIntroSequenceListener for the current created instance
 *
 * @param initialDelay delay for the first MIV to be shown
 *
 * @param materialIntroSequenceListener listener for MaterialIntroSequence events
 *
 * @param showSkip Whether to show the skip button for MIVs
 *
 * @param persistSkip If enabled, once the user clicks on skip button, all new MIVs will be skipped too, else even after the user clicks on skip
 * button and new MIVs are added after that, for e.g. for another fragment, the new MIVs will be shown
 */
fun Fragment.materialIntroSequence(
        initialDelay: Long? = null, materialIntroSequenceListener: MaterialIntroSequenceListener? = null, showSkip: Boolean? = null, persistSkip: Boolean? = null,
        func: MaterialIntroSequence.() -> Unit
): MaterialIntroSequence? {
    return if (activity == null) null
    else materialIntroSequence.apply {
        if (activity is MaterialIntroSequenceListener) {
            this?.materialIntroSequenceListener = activity as MaterialIntroSequenceListener
        }
        if (this@materialIntroSequence is MaterialIntroSequenceListener) {
            this?.materialIntroSequenceListener = this@materialIntroSequence
        }
        showSkip?.let {
            this?.showSkip = it
        }
        persistSkip?.let {
            this?.persistSkip = it
        }
        initialDelay?.let {
            this?.initialDelay = it
        }
        materialIntroSequenceListener?.let {
            this?.materialIntroSequenceListener = it
        }
        this?.func()
        this?.start()
    }
}