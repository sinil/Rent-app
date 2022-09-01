package com.riwal.rentalapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo.DetailedState.DISCONNECTED
import android.os.Bundle
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.riwal.rentalapp.BuildConfig
import com.riwal.rentalapp.common.extensions.eventbus.postStickyEvent
import com.riwal.rentalapp.main.MainView
import io.reactivex.schedulers.Schedulers
import net.danlew.android.joda.JodaTimeAndroid


class Application : Application(), Application.ActivityLifecycleCallbacks {


    /*---------------------------------------- Properties ----------------------------------------*/


    lateinit var mvcCoordinator: MvcCoordinator

    var isMainActivityCreated = false

    private var mixpanelApi: MixpanelAPI? = null


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.CRASH_REPORTING_ENABLED) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        }

        JodaTimeAndroid.init(this)

        mvcCoordinator = MvcCoordinator(applicationContext)

        registerActivityLifecycleCallbacks(this)

        ReactiveNetwork.observeNetworkConnectivity(applicationContext)
                .subscribeOn(Schedulers.io())
                .subscribe { connectivity -> postStickyEvent(connectivity.detailedState()) }

    }

    override fun onActivityCreated(view: Activity, savedInstanceState: Bundle?) {
        if (view is MainView) {
            isMainActivityCreated = true
        }
    }
    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        // Sometimes when you open the app when it's been in the background, network state is no longer correct
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connectivityState = connectivityManager.activeNetworkInfo?.detailedState ?: DISCONNECTED
        postStickyEvent(connectivityState)
    }

    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityDestroyed(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityStopped(activity: Activity) {}

    val mixpanel: MixpanelAPI
        @Synchronized get() {
            if (mixpanelApi == null) {
                mixpanelApi = MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_API_KEY)
            }
            return mixpanelApi!!
        }

    companion object {
        operator fun get(context: Context): com.riwal.rentalapp.Application {
            return context.applicationContext as com.riwal.rentalapp.Application
        }
    }


}