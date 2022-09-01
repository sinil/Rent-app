package com.riwal.rentalapp.common.ui

import androidx.annotation.CallSuper
import com.riwal.rentalapp.common.BetterBundle

interface ServiceLifecycleObserver {

    @CallSuper
    fun onViewCreate() {
    }

    @CallSuper
    fun onViewDestroy() {

    }

}

fun ServiceLifecycleObserver.observe(view: ObservableLifecycleService) {
    view.addLifecycleObserver(this)
}

fun ServiceLifecycleObserver.stopObserving(view: ObservableLifecycleService) {
    view.removeLifecycleObserver(this)
}