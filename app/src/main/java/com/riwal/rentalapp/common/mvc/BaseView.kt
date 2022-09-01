package com.riwal.rentalapp.common.mvc

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.RegisterControllerPreparationHandlerRequest
import com.riwal.rentalapp.ViewCreatedEvent
import com.riwal.rentalapp.common.extensions.android.activity
import com.riwal.rentalapp.common.extensions.android.runOnUiThread
import com.riwal.rentalapp.common.extensions.android.startActivity
import com.riwal.rentalapp.common.extensions.datetime.*
import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.transition.BasicActivityTransition
import kotlinx.coroutines.*
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import kotlin.reflect.KClass


open class BaseView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), MvcView, ObservableLifecycleView, CoroutineScope {


    /*--------------------------------------- Properties -----------------------------------------*/


    private var isCreated = false
    private var canAnimate = false

    private var coroutineScope = MainScope()
    override val coroutineContext
        get() = coroutineScope.coroutineContext


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (!isCreated) {

            postEvent(ViewCreatedEvent(viewToken = token(), view = this))

            onCreate()
            notifyCreate()
            isCreated = true
        }

        if (!coroutineScope.isActive) {
            coroutineScope = MainScope()
        }

        onAppear()
        notifyAppear()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        onDisappear()
        notifyDisappear()

        coroutineScope.cancel()
    }

    @CallSuper
    open fun onCreate() {

    }

    @CallSuper
    open fun onAppear() {
        canAnimate = true
    }

    @CallSuper
    open fun onDisappear() {

    }

    override fun notifyDataChanged() {
        runOnUiThread {
            updateUI(animated = canAnimate)
        }
    }

    open fun updateUI(animated: Boolean = true) {

    }


    /*---------------------------------- ObservableLifecycleView ---------------------------------*/


    override var lifecycleObservers: List<ViewLifecycleObserver> = emptyList()


    /*--------------------------------------- Open methods ---------------------------------------*/


    open fun onBackPressed(): Boolean = false


    /*------------------------------------------ Methods -----------------------------------------*/


    fun token(): Any {
        return this
    }

    fun <T : BaseView> addView(view: T, controllerPreparationHandler: ControllerPreparationHandler) {
        postEvent(RegisterControllerPreparationHandlerRequest(viewToken = view.token(), preparationHandler = controllerPreparationHandler))
        addView(view)
    }

    fun <T : BaseView> addView(view: T, index: Int, controllerPreparationHandler: ControllerPreparationHandler) {
        postEvent(RegisterControllerPreparationHandlerRequest(viewToken = view.token(), preparationHandler = controllerPreparationHandler))
        addView(view, index)
    }

    fun <T : BaseView> ViewGroup.addView(view: T, controllerPreparationHandler: ControllerPreparationHandler) {
        postEvent(RegisterControllerPreparationHandlerRequest(viewToken = view.token(), preparationHandler = controllerPreparationHandler))
        addView(view)
    }

    fun <T : BaseView> ViewGroup.addView(view: T, index: Int, controllerPreparationHandler: ControllerPreparationHandler) {
        postEvent(RegisterControllerPreparationHandlerRequest(viewToken = view.token(), preparationHandler = controllerPreparationHandler))
        addView(view, index)
    }

    fun <T : BaseActivity> startActivity(activityClass: KClass<T>, controllerPreparationHandler: ControllerPreparationHandler, transition: BasicActivityTransition? = null) {
        (activity as BaseActivity).startActivity(activityClass, controllerPreparationHandler = controllerPreparationHandler, transition = transition)
    }

    fun <T : BaseActivity> startActivity(activityClass: KClass<T>, transition: BasicActivityTransition) {
        activity.startActivity(activityClass, transition = transition)
    }


    /*---------------------------------------- Extensions ----------------------------------------*/


    fun DateTime.format(flags: Int) = format(context, flags)
    fun DateTime.toShortStyleString() = toShortStyleString(context)
    fun DateTime.toMediumStyleString() = toMediumStyleString(context)
    fun DateTime.toLongStyleString() = toLongStyleString(context)
    fun DateTime.toFullStyleString() = toFullStyleString(context)

    fun LocalDate.format(flags: Int) = format(context, flags)
    fun LocalDate.toShortStyleString() = toShortStyleString(context)
    fun LocalDate.toMediumStyleString() = toMediumStyleString(context)
    fun LocalDate.toLongStyleString() = toLongStyleString(context)
    fun LocalDate.toFullStyleString() = toFullStyleString(context)

    fun LocalTime.format(flags: Int) = format(context, flags)
    fun LocalTime.toShortStyleString() = toShortStyleString(context)

    fun Duration.toHumanizedString() = toHumanizedString(context)

    @JvmName("localDateRangeToShortStyleString")
    fun ClosedRange<LocalDate>.toShortStyleString() = toShortStyleString(context)

    @JvmName("localDateRangeToMediumStyleString")
    fun ClosedRange<LocalDate>.toMediumStyleString() = toMediumStyleString(context)

    @JvmName("localDateRangeToLongStyleString")
    fun ClosedRange<LocalDate>.toLongStyleString() = toLongStyleString(context)

    @JvmName("localTimeRangeToShortStyleString")
    fun ClosedRange<LocalTime>.toShortStyleString() = toShortStyleString(context)

    @JvmName("localDateRangeToFullStyleString")
    fun ClosedRange<LocalDate>.toFullStyleString() = toFullStyleString(context)

    @JvmName("dateTimeRangeToShortStyleString")
    fun ClosedRange<DateTime>.toShortStyleString() = toShortStyleString(context)

    @JvmName("dateTimeRangeToMediumStyleString")
    fun ClosedRange<DateTime>.toMediumStyleString() = toMediumStyleString(context)

    @JvmName("dateTimeRangeToLongStyleString")
    fun ClosedRange<DateTime>.toLongStyleString() = toLongStyleString(context)

    @JvmName("dateTimeRangeToFullStyleString")
    fun ClosedRange<DateTime>.toFullStyleString() = toFullStyleString(context)
}
