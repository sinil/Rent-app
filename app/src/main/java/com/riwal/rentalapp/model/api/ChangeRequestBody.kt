package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.common.extensions.datetime.isoString
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.api.ChangeRequestBody.Change
import com.riwal.rentalapp.model.api.ChangeRequestBody.ChangeField.*

data class ChangeRequestBody(
        val customer: CustomerBody,
        val orderNumber: String,
        val orderContact: Contact,
        val rentalType: String,
        val fleetNumber: String,
        val changes: List<Change>,
        val comments: String,
        val countryCode: Country,
        val purchaseOrder: String?,
        val project: Project) {

    enum class ChangeField {
        PROJECT_NAME,
        PROJECT_ADDRESS,
        PROJECT_LOCATION,
        PROJECT_CONTACT_NAME,
        PROJECT_CONTACT_PHONE_NUMBER,
        ON_RENT_DATE_TIME,
        OFF_RENT_DATE_TIME,
        PURCHASE_ORDER
    }

    class Change(
            val key: ChangeField,
            val value: String?
    )
}

fun Rental.differences(other: Rental): List<Change> {

    var changes: List<Change> = emptyList()

    if (project.name != other.project.name) {
        changes += Change(PROJECT_NAME, other.project.name)
    }
    if (project.address != other.project.address) {
        changes += Change(PROJECT_ADDRESS, other.project.address)
    }
    if (project.coordinate != other.project.coordinate) {
        changes += Change(PROJECT_LOCATION, "${other.project.coordinate!!.latitude}, ${other.project.coordinate!!.longitude}")
    }
    if (project.contactName != other.project.contactName) {
        changes += Change(PROJECT_CONTACT_NAME, other.project.contactName)
    }
    if (onRentDateTime != other.onRentDateTime) {
        changes += Change(ON_RENT_DATE_TIME, other.onRentDateTime.isoString)
    }
    if (offRentDateTime != other.offRentDateTime) {
        changes += Change(OFF_RENT_DATE_TIME, other.offRentDateTime?.isoString)
    }
    if (purchaseOrder != other.purchaseOrder) {
        changes += Change(PURCHASE_ORDER, other.purchaseOrder)
    }

    return changes
}