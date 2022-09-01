package com.riwal.rentalapp.common.ui

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.getString

object Snackbars {

    fun machineAddedToCart(parent: View, duration: Int = Snackbar.LENGTH_LONG) = Snackbar.make(parent, parent.getString(R.string.machine_order_added)!!, duration)

}