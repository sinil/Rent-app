package com.riwal.rentalapp.common

import android.os.Bundle
import com.riwal.rentalapp.common.extensions.android.get
import com.riwal.rentalapp.common.extensions.android.set

class BetterBundle(val bundle: Bundle) {

    inline operator fun <reified T : Any> get(key: String) = bundle.get<T>(key)
    inline operator fun <reified T : Any> set(key: String, value: T?) = bundle.set(key, value)

}