package com.riwal.rentalapp.common.mvc

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.ViewTreeObserver
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.RegisterControllerPreparationHandlerRequest
import com.riwal.rentalapp.ViewCreatedEvent
import com.riwal.rentalapp.common.ActivityResultException
import com.riwal.rentalapp.common.BetterBundle
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.datetime.*
import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.transition.BasicActivityTransition
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import kotlin.collections.set
import kotlin.reflect.KClass

open class BaseActivity : AppCompatActivity(), MvcView, ObservableLifecycleView, CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    override var lifecycleObservers: List<ViewLifecycleObserver> = emptyList()

    private val isLaidOut
        get() = ViewCompat.isLaidOut(contentView)

    private val activityResultObservers: MutableMap<Int, SingleSubject<Intent>> = mutableMapOf()
    private val permissionRequestObservers: MutableMap<String, SingleSubject<Boolean>> = mutableMapOf()

    private var canAnimate = false

    val statusBarHeight: Int
        get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId)
            }
            return -1
        }

    var statusBarColor
        @ColorInt get() = window.statusBarColor
        set(value) {
            window.statusBarColor = value
        }

    val activityManager
        get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    fun setFullscreen() {
        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postEvent(ViewCreatedEvent(viewToken = this::class, view = this))

        if (savedInstanceState != null) {
            notifyRestore(BetterBundle(savedInstanceState))
        }

        onCreate()
        notifyCreate()

        // Disgusting, but this prevents some weird pop-ins / flashes when using transition animations
        // Solution: https://stackoverflow.com/questions/26600263/how-do-i-prevent-the-status-bar-and-navigation-bar-from-animating-during-an-acti
        postponeEnterTransition()
        val decor = window.decorView
        decor.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                decor.viewTreeObserver.removeOnPreDrawListener(this)
                startPostponedEnterTransition()
                return true
            }
        })

    }

    override fun onStart() {
        super.onStart()

        // Disgusting, but apparently after onAttachedToWindow the View still isn't ready for animation because the subviews may still be unattached T_T
        // Solution: https://stackoverflow.com/questions/26819429/cannot-start-this-animator-on-a-detached-view-reveal-effect
        if (isLaidOut) {
            onAppear()
            notifyAppear()
        } else {
            contentView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                    contentView.removeOnLayoutChangeListener(this)
                    onAppear()
                    notifyAppear()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        notifyResume()
    }

    override fun onPause() {
        super.onPause()
        notifyPause()
    }

    override fun onStop() {
        super.onStop()
        onDisappear()
        notifyDisappear()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        notifySave(BetterBundle(outState))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val request = activityResultObservers.keys.firstOrNull { it == requestCode } ?: return
        val handler = activityResultObservers.remove(request)!!

        if (resultCode == Activity.RESULT_OK) {
            handler.onSuccess(data ?: Intent())
        } else {
            val error = ActivityResultException(resultCode)
            handler.onError(error)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        clearFocusAndHideKeyboard()
        return super.dispatchTouchEvent(ev)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val permission = permissionRequestObservers.keys.find { Math.abs(it.hashCode()) == requestCode }
                ?: return
        val permissionGranted = grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        val handler = permissionRequestObservers.remove(permission)!!
        handler.onSuccess(permissionGranted)
    }

    override fun notifyDataChanged() {
        runOnUiThread {
            updateUI(animated = canAnimate)
        }
    }

    open fun updateUI(animated: Boolean = true) {

    }


    /*---------------------------------- ObservableLifecycleView ---------------------------------*/


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

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        notifyDestroy()
        cancel() // Cancels the supervising Job of this coroutine scope
    }


    /*------------------------------------------ Methods -----------------------------------------*/


    fun <T : BaseActivity> startActivity(activityClass: KClass<T>, flags: Int = 0, controllerPreparationHandler: ControllerPreparationHandler, transition: BasicActivityTransition? = null) {
        postEvent(RegisterControllerPreparationHandlerRequest(viewToken = activityClass, preparationHandler = controllerPreparationHandler))
        startActivity(activityClass, flags, transition = transition)
    }

    fun <T : Any> startActivityForResult(activityClass: KClass<T>, extras: Map<String, Any?> = emptyMap(), transition: BasicActivityTransition? = null): Single<Intent> {
        val observableResult = startActivityForResult(Intent(this, activityClass.java).putExtras(extras))
        overridePendingTransition(transition ?: defaultExitTransitionForActivity(this::class))
        return observableResult
    }

    override fun finish() {
        finish(transition = defaultExitTransitionForActivity(this::class))
    }

    fun finish(transition: BasicActivityTransition) {
        super.finish()
        overridePendingTransition(transition)
    }

    fun onUiThread(action: () -> Unit) {
        if (isFinishing) {
            return
        }
        runOnUiThread(action)
    }


    /*---------------------------------------- Open Methods --------------------------------------*/


    open fun requestPermission(permission: String): Single<Boolean> {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return SingleSubject.just(true)
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return SingleSubject.just(true)
        }

        if (permission in permissionRequestObservers) {
            return permissionRequestObservers[permission]!!
        }

        val handler = SingleSubject.create<Boolean>()
        permissionRequestObservers[permission] = handler
        requestPermissions(arrayOf(permission), Math.abs(permission.hashCode()))
        return handler
    }

    // TODO: The result is lost if the calling activity is destroyed while the started activity is active
    open fun startActivityForResult(intent: Intent): Single<Intent> {
        val requestCode = (Math.random() * Short.MAX_VALUE * 2).toInt()
        val handler = SingleSubject.create<Intent>()
        activityResultObservers[requestCode] = handler
        startActivityForResult(intent, requestCode)
        return handler
    }


    /*----------------------------------------- Extensions ---------------------------------------*/


    fun Int.dp() = dp(this)

    fun Drawable.setTintList(@ColorRes resId: Int) = setTintList(resources.getColorStateList(resId))

    fun DateTime.toShortStyleString() = toShortStyleString(this@BaseActivity)
    fun DateTime.toMediumStyleString() = toMediumStyleString(this@BaseActivity)
    fun DateTime.toLongStyleString() = toLongStyleString(this@BaseActivity)
    fun DateTime.toFullStyleString() = toFullStyleString(this@BaseActivity)

    fun LocalDate.toShortStyleString() = toShortStyleString(this@BaseActivity)
    fun LocalDate.toMediumStyleString() = toMediumStyleString(this@BaseActivity)
    fun LocalDate.toLongStyleString() = toLongStyleString(this@BaseActivity)
    fun LocalDate.toFullStyleString() = toFullStyleString(this@BaseActivity)

    fun LocalTime.toShortStyleString() = toShortStyleString(this@BaseActivity)

    fun Duration.toHumanizedString() = toHumanizedString(this@BaseActivity)

    @JvmName("localDateRangeToShortStyleString")
    fun ClosedRange<LocalDate>.toShortStyleString() = toShortStyleString(this@BaseActivity)

    @JvmName("localDateRangeToMediumStyleString")
    fun ClosedRange<LocalDate>.toMediumStyleString() = toMediumStyleString(this@BaseActivity)

    @JvmName("localDateRangeToLongStyleString")
    fun ClosedRange<LocalDate>.toLongStyleString() = toLongStyleString(this@BaseActivity)

    @JvmName("localTimeRangeToShortStyleString")
    fun ClosedRange<LocalTime>.toShortStyleString() = toShortStyleString(this@BaseActivity)

    @JvmName("localDateRangeToFullStyleString")
    fun ClosedRange<LocalDate>.toFullStyleString() = toFullStyleString(this@BaseActivity)

    @JvmName("dateTimeRangeToShortStyleString")
    fun ClosedRange<DateTime>.toShortStyleString() = toShortStyleString(this@BaseActivity)

    @JvmName("dateTimeRangeToMediumStyleString")
    fun ClosedRange<DateTime>.toMediumStyleString() = toMediumStyleString(this@BaseActivity)

    @JvmName("dateTimeRangeToLongStyleString")
    fun ClosedRange<DateTime>.toLongStyleString() = toLongStyleString(this@BaseActivity)

    @JvmName("dateTimeRangeToFullStyleString")
    fun ClosedRange<DateTime>.toFullStyleString() = toFullStyleString(this@BaseActivity)
}