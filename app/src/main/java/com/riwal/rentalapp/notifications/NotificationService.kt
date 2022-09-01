package com.riwal.rentalapp.notifications

import com.google.firebase.messaging.RemoteMessage
import com.riwal.rentalapp.common.ui.ObservableLifecycleService
import com.riwal.rentalapp.model.Rental

enum class NotificationType(val value: String) {
    OFF_RENT("OFF_RENT")
}

interface NotificationService : ObservableLifecycleService {

    var dataSource: DataSource
    var delegate: Delegate

    fun showNotification(type: NotificationType, rental: Rental)

    interface DataSource

    interface Delegate {
        fun onNewTokenReceived(token: String)
        fun onMessageReceived(message: RemoteMessage)
    }

}