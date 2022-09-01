package com.riwal.rentalapp.common

class ActivityResultException(val resultCode: Int, message: String = "The Activity result did not return with an OK status") : Exception(message)