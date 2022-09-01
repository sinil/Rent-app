package com.riwal.rentalapp.common.ui

import com.riwal.rentalapp.common.BetterBundle

interface ObservableLifecycleView {

    var lifecycleObservers: List<ViewLifecycleObserver>

    fun addLifecycleObserver(observer: ViewLifecycleObserver) {
        if (observer !in lifecycleObservers) {
            lifecycleObservers += observer
        }
    }

    fun removeLifecycleObserver(observer: ViewLifecycleObserver) {
        lifecycleObservers -= observer
    }

    fun notifyCreate() {
        lifecycleObservers.forEach { it.onViewCreate() }
    }

    fun notifyAppear() {
        lifecycleObservers.forEach { it.onViewAppear() }
    }

    fun notifyResume() {
        lifecycleObservers.forEach { it.onViewResume() }
    }

    fun notifyPause() {
        lifecycleObservers.forEach { it.onViewPause() }
    }

    fun notifyDisappear() {
        lifecycleObservers.forEach { it.onViewDisappear() }
    }

    fun notifySave(state: BetterBundle) {
        lifecycleObservers.forEach { it.onViewSave(state) }
    }

    fun notifyRestore(savedState: BetterBundle) {
        lifecycleObservers.forEach { it.onViewRestore(savedState) }
    }

    fun notifyDestroy() {
        lifecycleObservers.forEach { it.onViewDestroy() }
    }


}

fun ObservableLifecycleView.onViewAppear(handler: () -> Unit) {
    addLifecycleObserver(object : ViewLifecycleObserver {
        override fun onViewAppear() {
            super.onViewAppear()
            handler()
            removeLifecycleObserver(this)
        }
    })
}