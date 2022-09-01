package com.riwal.rentalapp.common.ui

interface ObservableLifecycleService {

    var lifecycleObservers: List<ServiceLifecycleObserver>

    fun addLifecycleObserver(observer: ServiceLifecycleObserver) {
        if (observer !in lifecycleObservers) {
            lifecycleObservers += observer
        }
    }

    fun removeLifecycleObserver(observer: ServiceLifecycleObserver) {
        lifecycleObservers -= observer
    }

    fun notifyCreate() {
        lifecycleObservers.forEach { it.onViewCreate() }
    }

    fun notifyDestroy() {
        lifecycleObservers.forEach { it.onViewDestroy() }
    }


}