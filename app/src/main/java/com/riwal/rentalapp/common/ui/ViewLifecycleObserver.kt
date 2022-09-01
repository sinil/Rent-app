package com.riwal.rentalapp.common.ui

import com.riwal.rentalapp.common.BetterBundle

interface ViewLifecycleObserver {
    fun onViewCreate() {}
    fun onViewAppear() {}
    fun onViewResume() {}
    fun onViewPause() {}
    fun onViewDisappear() {}
    fun onViewSave(state: BetterBundle) {}
    fun onViewRestore(savedState: BetterBundle) {}
    fun onViewDestroy() {}
}

fun ViewLifecycleObserver.observe(view: ObservableLifecycleView) {
    view.addLifecycleObserver(this)
}

fun ViewLifecycleObserver.stopObserving(view: ObservableLifecycleView) {
    view.removeLifecycleObserver(this)
}