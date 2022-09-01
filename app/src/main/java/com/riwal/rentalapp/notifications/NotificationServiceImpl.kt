package com.riwal.rentalapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.RemoteMessage
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.mvc.BaseNotificationService
import com.riwal.rentalapp.common.extensions.android.Intent
import com.riwal.rentalapp.common.extensions.android.notificationManager
import com.riwal.rentalapp.common.extensions.android.postNotification
import com.riwal.rentalapp.common.extensions.json.toJson
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.notifications.NotificationService.DataSource
import com.riwal.rentalapp.notifications.NotificationService.Delegate
import com.riwal.rentalapp.notifications.NotificationType.OFF_RENT
import org.joda.time.Days.daysBetween
import org.joda.time.LocalDateTime.now


class NotificationServiceImpl : BaseNotificationService(), NotificationService {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onNewToken(token: String) {
        delegate.onNewTokenReceived(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        delegate.onMessageReceived(message)
    }


    /*-------------------------------- NotificationService -------------------------------*/


    override fun showNotification(type: NotificationType, rental: Rental) {

        if (isAndroidOreoOrLater()) {
            // Oreo notification requires a notification channel.
            configureOffRentNotificationChannel()
        }

        when (type) {
            OFF_RENT -> showOffRentNotification(rental)
        }

    }


    /*-------------------------------------- Private methods -------------------------------------*/


    private fun isAndroidOreoOrLater() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configureOffRentNotificationChannel() {
        val channel = NotificationChannel(OFF_RENT_ALERT_NOTIFICATION_CHANNEL_ID, getString(R.string.notification_channel_off_rent), IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.lightColor = getColor(R.color.colorPrimary)
        notificationManager.createNotificationChannel(channel)
    }

    private fun showOffRentNotification(rental: Rental) {

        val notificationIntent = Intent(NotificationActionServiceImpl::class, OFF_RENT.value, mapOf("rental" to rental.toJson()))
        val contentIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        val days = daysBetween(now().toLocalDate(), rental.offRentDateTime!!.toLocalDate()).days

        postNotification(
                id = rental.hashCode(),
                channelId = NotificationServiceImpl.OFF_RENT_ALERT_NOTIFICATION_CHANNEL_ID,
                title = getString(R.string.notification_off_rent_will_expire_title),
                message = getString(R.string.notification_off_rent_will_expire_message, rental.rentalType, days.toString()),
                onClickIntent = contentIntent
        )
    }


    /*----------------------------------------- Constants ----------------------------------------*/


    companion object {
        const val OFF_RENT_ALERT_NOTIFICATION_CHANNEL_ID = "OFF_RENT_ALERT_NOTIFICATION_CHANNEL_ID"
    }
}