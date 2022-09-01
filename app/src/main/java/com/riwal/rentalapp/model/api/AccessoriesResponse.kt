package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.BuildConfig
import com.riwal.rentalapp.common.extensions.core.preapend
import com.riwal.rentalapp.model.Accessory

class AccessoriesResponse(private val Name: String,
                          private val ImageUrl: String,
                          private val Qty: List<Int>) {

    fun toAccessory() = Accessory(
            name = Name,
            imageUrl = ImageUrl.preapend { BuildConfig.MY_RIWAL_URL },
            quantities = Qty
    )
}
