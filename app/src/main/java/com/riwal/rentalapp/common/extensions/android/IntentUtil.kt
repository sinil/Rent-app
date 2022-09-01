package com.riwal.rentalapp.common.extensions.android

import android.content.Intent
import android.os.Bundle
import com.riwal.rentalapp.common.extensions.json.fromJson
import com.riwal.rentalapp.common.extensions.json.fromListJson

fun Intent.putExtras(extras: Map<String, Any?>): Intent {
    val bundle = Bundle()
    bundle += extras
    return putExtras(bundle)
}

inline fun <reified T> Intent.getObjectFromJsonExtra(name: String): T? = fromJson(getStringExtra(name))

inline fun <reified T> Intent.getListFromJsonExtra(name: String): List<T>? = fromListJson(getStringExtra(name))