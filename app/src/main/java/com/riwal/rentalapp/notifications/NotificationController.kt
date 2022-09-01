package com.riwal.rentalapp.notifications

import com.google.firebase.messaging.RemoteMessage
import com.riwal.rentalapp.common.extensions.json.fromJson
import com.riwal.rentalapp.model.SessionManager
import com.riwal.rentalapp.model.api.RentalResponse
import com.riwal.rentalapp.notifications.NotificationType.OFF_RENT

class NotificationController(
        val service: NotificationService,
        val sessionManager: SessionManager
) : NotificationService.DataSource, NotificationService.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    private val isLoggedIn
        get() = sessionManager.isLoggedIn


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        service.dataSource = this
        service.delegate = this
    }


    /*---------------------------------------- DataSource ----------------------------------------*/


    /*----------------------------------------- Delegate -----------------------------------------*/


    override fun onNewTokenReceived(token: String) {
        sessionManager.onNewNotificationToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (!isLoggedIn) {
            return
        }

        val type = NotificationType.valueOf(message.data["type"]!!)

        when (type) {
            OFF_RENT -> {
                val rentalResponse: RentalResponse = fromJson(message.data["payload"], true)!!
                val rental = rentalResponse.toRental()
                service.showNotification(OFF_RENT, rental)
            }
        }

    }

}