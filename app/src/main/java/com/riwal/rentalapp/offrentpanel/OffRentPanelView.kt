package com.riwal.rentalapp.offrentpanel

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.offrentpanel.OffRentPanelController.Style
import org.joda.time.LocalDate
import org.joda.time.LocalTime

interface OffRentPanelView : MvcView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun changeMachinePickupLocation()

    interface DataSource {

        // Limits

        fun validOffRentDateRange(view: OffRentPanelView): ClosedRange<LocalDate>
        fun validTimeRangeForOffRentDate(view: OffRentPanelView): ClosedRange<LocalTime>

        // Defaults

        fun defaultOffRentDate(view: OffRentPanelView): LocalDate
        fun defaultOffRentTime(view: OffRentPanelView): LocalTime

        // Chosen values

        fun offRentDate(view: OffRentPanelView): LocalDate?
        fun offRentTime(view: OffRentPanelView): LocalTime?

        // Other

        fun style(view: OffRentPanelView): Style
        fun country(view: OffRentPanelView): Country
        fun canSelectOffRentTime(view: OffRentPanelView): Boolean
        fun canConfirmOffRent(view: OffRentPanelView): Boolean
    }

    interface Delegate {
        fun onOffRentDatePicked(date: LocalDate)
        fun onOffRentTimePicked(time: LocalTime)
        fun onConfirmSelected()
        fun pickupOptionChanged(location: String)
    }

}