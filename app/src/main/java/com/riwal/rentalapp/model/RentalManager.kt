package com.riwal.rentalapp.model

import android.annotation.SuppressLint
import com.riwal.rentalapp.common.extensions.core.week
import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.model.Notification.*
import com.riwal.rentalapp.model.api.BackendClient
import com.riwal.rentalapp.model.api.InvoiceResponse
import com.riwal.rentalapp.model.api.MyQuotationsResponse
import com.riwal.rentalapp.model.api.QuotationsResponse
import com.riwal.rentalapp.requestaccountform.Account
import okhttp3.ResponseBody
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now
import org.joda.time.LocalDateTime

class RentalManager(
        private val sessionManager: SessionManager,
        private val backend: BackendClient,
        private val machinesManager: MachinesManager
) {


    /*--------------------------------------- Properties -----------------------------------------*/


    val user
        get() = sessionManager.user


    /*----------------------------------------- Methods ------------------------------------------*/


    suspend fun requestAccount(account: Account, country: Country) {
        try {
            backend.sendAccountRequest(account, country)
            postEvent(ACCOUNT_REQUEST_SUCCEEDED)
        } catch (error: Exception) {
            postEvent(ACCOUNT_REQUEST_FAILED)
        }
    }

    suspend fun getRentals(dateRange: ClosedRange<LocalDate>, customer: Customer) = backend
            .getRentals(customer, dateRange)
            .sortedByDescending { it.onRentDateTime }
            .map { rental ->
                val machine = machinesManager.machineForRental(rental)
                rental.copy(machine = machine)
            }

    suspend fun getAccessoriesOnRent(dateRange: ClosedRange<LocalDate>, customer: Customer): List<AccessoriesOnRent> {
        return backend.getAccessoriesOnRent(customer, dateRange)
    }

    suspend fun getInvoices(customer: Customer, dateRange: ClosedRange<LocalDate>): InvoiceResponse {
        return backend.getInvoices(customer, dateRange)
    }

    suspend fun downloadInvoice(customerId: String, invoiceId: String): ResponseBody {
        return backend.downloadInvoice(customerId, invoiceId)
    }
    
    suspend fun getQuotations(customer: Customer, dateRange: ClosedRange<LocalDate>): MyQuotationsResponse{
        return backend.getQuotations(customer, dateRange)
    }

    suspend fun downloadQuotation(customerId: String, quotationId: String): ResponseBody {
        return backend.downloadQuotation(customerId, quotationId)
    }

    // Apparently, a machine can already be at the customer before its on rent date. It then
    // already has the "on rent" status. Thus to find all "current" rentals, we need to look
    // before today also (a week seems safe). We also need to look at later dates than today,
    // because a machine can still be at the customer after the off rent date (with status
    // status waiting pickup). Again a week seems safe. We then run into the situation that we
    // may get more rentals for the same machine, with possibly also other statuses than
    // "on rent" and "pending pickup", so we first remove all statuses we are not interested in
    // (unknown, pending delivery, closed), then sort on on rent date (assuming rental periods
    // cannot overlap), and then only keep the machine with the earliest on rent date if it
    // occurs more than once (distinct keeps only the first occurrence).

    private suspend fun getCurrentRentals(customer: Customer) =
            getRentals(dateRange = (now() - 1.week())..(now() + 1.week()), customer = customer)
                    .sortedByDescending { it.onRentDateTime }
                    .distinctBy { rental -> rental.fleetNumber }
                    .filter { it.status == RentalStatus.ON_RENT || it.status == RentalStatus.PENDING_PICKUP }
                    .map { rental ->
                        val machine = machinesManager.machineForRental(rental)
                        rental.copy(machine = machine)
                    }

    suspend fun getCurrentRental(fleetNumber: String, customer: Customer) =
            getCurrentRentals(customer).firstOrNull { currentRentals ->
                currentRentals.fleetNumber == fleetNumber
            }

    suspend fun getRentalDetail(customer: Customer, inventTransId: String) = backend.getRentalDetail(customer, inventTransId)

    @SuppressLint("CheckResult")
    suspend fun requestOffRent(rentals: List<Rental>, dateTime: LocalDateTime, notes: String, customer: Customer, country: Country, pickupLocation: String?) {
        try {
            backend.sendOffRentRequest(rentals, dateTime, notes, customer, country, pickupLocation)
            postEvent(OFF_RENT_REQUEST_SUCCEEDED)
        } catch (error: Exception) {
            postEvent(OFF_RENT_REQUEST_FAILED)
        }
    }

    @SuppressLint("CheckResult")
    suspend fun requestOffRentAccessories(accessories: List<AccessoriesOnRent>, dateTime: LocalDateTime, notes: String, customer: Customer, country: Country, pickupLocation: String?) {
        try {
            backend.sendOffRentAccessoriesRequest(accessories, dateTime, notes, customer, country, pickupLocation)
            postEvent(OFF_RENT_REQUEST_SUCCEEDED)
        } catch (error: Exception) {
            postEvent(OFF_RENT_REQUEST_FAILED)
        }
    }


    @SuppressLint("CheckResult")
    suspend fun requestCancelRent(rentals: List<Rental>, dateTime: LocalDateTime, notes: String, customer: Customer, country: Country) {
        try {
            backend.sendCancelRentRequest(rentals, dateTime, notes, customer, country)
            postEvent(CANCEL_RENT_REQUEST_SUCCEEDED)
        } catch (error: Exception) {
            postEvent(CANCEL_RENT_REQUEST_FAILED)
        }
    }

    @SuppressLint("CheckResult")
    suspend fun requestCancelAccessories(rentals: List<AccessoriesOnRent>, dateTime: LocalDateTime, notes: String, customer: Customer, country: Country) {
        try {
            backend.sendCancelAccessoriesRequest(rentals, dateTime, notes, customer, country)
            postEvent(CANCEL_RENT_REQUEST_SUCCEEDED)
        } catch (error: Exception) {
            postEvent(CANCEL_RENT_REQUEST_FAILED)
        }
    }

    @SuppressLint("CheckResult")
    suspend fun requestChange(originalRental: Rental, changedRental: Rental, comments: String, customer: Customer, country: Country) {
        try {
            backend.sendChangeRequest(originalRental, changedRental, comments, customer, country)
            postEvent(CHANGE_REQUEST_SUCCEEDED)
        } catch (error: Exception) {
            postEvent(CHANGE_REQUEST_FAILED)
        }
    }

    @SuppressLint("CheckResult")
    suspend fun requestChange(originalAccessories: AccessoriesOnRent, changedAccessories: AccessoriesOnRent, comments: String, customer: Customer, country: Country) {
        try {
            backend.sendChangeRequest(originalAccessories, changedAccessories, comments, customer, country)
            postEvent(CHANGE_REQUEST_SUCCEEDED)
        } catch (error: Exception) {
            postEvent(CHANGE_REQUEST_FAILED)
        }
    }

    @SuppressLint("CheckResult")
    suspend fun reportBreakdown(rental: Rental, comments: String, customer: Customer, country: Country, contactPhoneNumber: String?) {
        try {
            backend.sendBreakdownRequest(rental, comments, customer, country, contactPhoneNumber)
            postEvent(SEND_BREAKDOWN_REPORT_SUCCEEDED)
        } catch (error: Exception) {
            postEvent(SEND_BREAKDOWN_REPORT_FAILED)
        }
    }

    @SuppressLint("CheckResult")
    suspend fun sendFeedback(rating: String, message: String?, email: String?, country: Country) {
        try {
            backend.sendFeedback(rating, message, email, country)
            postEvent(SEND_FEEDBACK_SUCCEEDED)
        } catch (error: Exception) {
            postEvent(SEND_FEEDBACK_FAILED)
        }
    }

}