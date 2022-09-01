package com.riwal.rentalapp.common.extensions.rentalapp

import android.content.Context
import android.net.ConnectivityManager
import com.riwal.rentalapp.R
import com.riwal.rentalapp.model.api.RentalAppServerException
import retrofit2.HttpException
import java.lang.IllegalStateException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.rentalAppMessage(context: Context): String = when (this) {
    is RentalAppServerException -> localizedMessage(context)
    is SocketTimeoutException -> context.getString(R.string.error_timeout)
    is ConnectException -> context.getString(R.string.error_no_internet_connection_try_again)
    is UnknownHostException -> context.getString(R.string.error_no_internet_connection_try_again)
    is HttpException -> if (this.isUnauthorized()) context.getString(R.string.unknown_username_password_combination) else context.getString(R.string.error_unknown)
    is IllegalStateException -> if (this.hasNoCustomers()) context.getString(R.string.no_customers_found) else context.getString(R.string.error_unknown)
    else -> context.getString(R.string.error_unknown)
}

// TODO: Once A4U properly returns UNAUTHORIZED instead of FORBIDDEN, we can remove FORBIDDEN
fun HttpException.isUnauthorized() = code() in listOf(HttpURLConnection.HTTP_UNAUTHORIZED, HttpURLConnection.HTTP_FORBIDDEN)

fun IllegalStateException.hasNoCustomers(): Boolean {
    return if (message != null)
        message!!.contains("customer")
    else false
}

fun Throwable.isServerDown(connectivityManager: ConnectivityManager): Boolean {
    if (cause != null) {
        val networkInfo = connectivityManager.activeNetworkInfo
        val hasInternetConnection = (networkInfo != null && networkInfo.isConnected)
        return (hasInternetConnection && cause is UnknownHostException)
    }
    return false
}