package com.riwal.rentalapp.common.extensions.android

import android.app.Service
import android.content.Intent
import kotlin.reflect.KClass

fun <T : Any> Service.startActivity(activityClass: KClass<T>) = startActivity(Intent(this, activityClass.java))

fun <T : Any> Service.startActivity(activityClass: KClass<T>, flags: Int) {
    val intent = Intent(this, activityClass.java)
    intent.flags = flags
    startActivity(intent)
}