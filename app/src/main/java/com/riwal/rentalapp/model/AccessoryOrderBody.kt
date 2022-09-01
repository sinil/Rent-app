package com.riwal.rentalapp.model

data class AccessoryOrderBody(var name: String, var imageURL: String?, var quantity: Int)

data class AccessoryOrder(var accessory: Accessory, var quantity: Int)

fun AccessoryOrder.toAccessoryOrderBody() = AccessoryOrderBody(
        name = accessory.name,
        imageURL = accessory.imageUrl,
        quantity = quantity
)