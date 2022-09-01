package com.riwal.rentalapp.common.extensions.android.graphics

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun FragmentManager.replace(@IdRes containerViewId: Int, fragment: Fragment) {
    beginTransaction()
            .replace(containerViewId, fragment)
            .commit()
}