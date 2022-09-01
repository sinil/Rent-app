package com.riwal.rentalapp.common.extensions.android

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.util.TypedValue
import androidx.annotation.AnyRes
import androidx.annotation.AttrRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.riwal.rentalapp.R
import kotlin.reflect.KClass

val Context.activity: Activity?
    get() = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.activity
        else -> null
    }

val Context.notificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


fun <T : Any> Context.Intent(activityClass: KClass<T>, action: String, extras: Map<String, Any?>): Intent {
    val intent = Intent(this, activityClass.java)
    intent.action = action
    intent.putExtras(extras)
    return intent
}

fun <T : Activity> Context.activityInfoForActivity(activityClass: KClass<T>): ActivityInfo? {
    val activitiesInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).activities
    return activitiesInfo.find { it.name == activityClass.qualifiedName }
}


fun Context.hasPermission(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED

fun Context.postNotification(id: Int, channelId: String, title: String, message: String, onClickIntent: PendingIntent) {

    val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(onClickIntent)
            .setSound(notificationSound)
            .setTicker(title)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(Notification.PRIORITY_MAX)
            .setAutoCancel(true)
            .build()

    notificationManager.notify(id, notification)
}

@AnyRes
fun Context.resourceIdForAttributeId(@AttrRes attributeId: Int): Int {
    val outValue = TypedValue()
    theme.resolveAttribute(attributeId, outValue, true)
    return outValue.resourceId
}

fun Context.drawableForAttributeId(@AttrRes attributeId: Int): Drawable? {
    val resourceId = resourceIdForAttributeId(attributeId)
    return try {
        resources.getDrawable(resourceId, theme)
    } catch (exception: Resources.NotFoundException) {
        null
    }
}
