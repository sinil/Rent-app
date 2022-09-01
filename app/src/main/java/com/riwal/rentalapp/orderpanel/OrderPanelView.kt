package com.riwal.rentalapp.orderpanel

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.MachineOrder.OffRentDateStrictness
import org.joda.time.LocalDate
import org.joda.time.LocalTime

interface OrderPanelView : MvcView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun notifyCannotStartRentInWeekend()

    interface DataSource {

        // Limits

        fun validOnRentDateRange(view: OrderPanelView): ClosedRange<LocalDate>
        fun validTimeRangeForOnRentDate(view: OrderPanelView): ClosedRange<LocalTime>
        fun validOffRentDateRange(view: OrderPanelView): ClosedRange<LocalDate>
        fun validTimeRangeForOffRentDate(view: OrderPanelView): ClosedRange<LocalTime>

        // Defaults

        fun defaultOnRentDate(view: OrderPanelView): LocalDate
        fun defaultOnRentTime(view: OrderPanelView): LocalTime
        fun defaultOffRentDate(view: OrderPanelView): LocalDate
        fun defaultOffRentTime(view: OrderPanelView): LocalTime

        // Chosen values

        fun onRentDate(view: OrderPanelView): LocalDate?
        fun onRentTime(view: OrderPanelView): LocalTime?
        fun offRentDateStrictness(view: OrderPanelView): OffRentDateStrictness
        fun offRentDate(view: OrderPanelView): LocalDate?
        fun offRentTime(view: OrderPanelView): LocalTime?

        // Other

        fun canSelectOnRentTime(view: OrderPanelView): Boolean
        fun canSelectOffRentStrictness(view: OrderPanelView): Boolean
        fun canSelectOffRentDate(view: OrderPanelView): Boolean
        fun canSelectOffRentTime(view: OrderPanelView): Boolean
        fun canAddToOrder(view: OrderPanelView): Boolean
        fun country(view: OrderPanelView): Country

    }

    interface Delegate {
        fun onOnRentDatePicked(date: LocalDate)
        fun onOnRentTimePicked(time: LocalTime)
        fun onOffRentCertaintySelected(strictness: OffRentDateStrictness)
        fun onOffRentDatePicked(date: LocalDate)
        fun onOffRentTimePicked(time: LocalTime)
        fun onConfirmSelected()
    }

}