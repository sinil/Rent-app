package com.riwal.rentalapp.common.extensions.eventbus

import org.greenrobot.eventbus.EventBus

fun postEvent(event: Any) {
    EventBus.getDefault().post(event)
}

fun postStickyEvent(event: Any) {
    EventBus.getDefault().postSticky(event)
}
