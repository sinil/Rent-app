package com.riwal.rentalapp.model.api

import android.content.Context
import com.riwal.rentalapp.R


/*---------------------------------------- Generic errors-----------------------------------------*/


sealed class RentalAppServerException(val error: Map<String, Any?>) : Exception() {
    abstract fun localizedMessage(context: Context): String
}


class InternalServerException(error: Map<String, Any?>) : RentalAppServerException(error) {
    override fun localizedMessage(context: Context) = context.getString(R.string.error_something_went_wrong, errorCode)

    companion object {
        const val errorCode = "INTERNAL_SERVER_ERROR"
    }
}

class NoCustomersException(error: Map<String, Any?>) : RentalAppServerException(error) {
    override fun localizedMessage(context: Context) = "NO CUSTOMERS"

    companion object {
        const val errorCode = "NO_CUSTOMERS"
    }
}

class NotFoundException(error: Map<String, Any?>) : RentalAppServerException(error) {
    override fun localizedMessage(context: Context) = context.getString(R.string.error_something_went_wrong, error["code"])
}

class ClientException(error: Map<String, Any?>) : RentalAppServerException(error) {
    override fun localizedMessage(context: Context) = context.getString(R.string.error_something_went_wrong, error["code"])
}