package com.riwal.rentalapp.common.extensions.android

import android.view.Menu

val Menu.items
    get() = (0 until size()).map { getItem(it) }