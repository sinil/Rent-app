package com.riwal.rentalapp.offrentpanel

import com.riwal.rentalapp.common.extensions.datetime.*
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Machine
import com.riwal.rentalapp.model.ValidOffRentPeriodCalculator
import com.riwal.rentalapp.offrentpanel.OffRentPanelController.Style.SINGLE_MACHINE
import org.joda.time.DateTime.now
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime

class OffRentPanelController(val view: OffRentPanelView, val country: Country) : OffRentPanelView.DataSource, OffRentPanelView.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    enum class Style {
        SINGLE_MACHINE,
        MULTIPLE_MACHINES
    }

    var style: Style = SINGLE_MACHINE
    var delegate: Delegate? = null
    lateinit var machine: Machine

    private val validOffRentPeriodCalculator = ValidOffRentPeriodCalculator(sameDayOffRentCutoffTime = country.sameDayOffRentCutoffTime)

    private var offRentDate: LocalDate? = null
    private var offRentTime: LocalTime? = null
    private var isAvailableForPickup: String? = null

    private val defaultOffRentDate
        get() = validOffRentPeriod.start.toLocalDate()!!

    private val defaultOffRentTime
        get() = if (offRentDate == today) now().roundUpToNearestHour().toLocalTime()!! else "08:00".toLocalTime()!!

    private val validOffRentPeriod
        get() = validOffRentPeriodCalculator.validOffRentPeriod()

    private val validTimeRangeForOffRentDate: ClosedRange<LocalTime>
        get() {
            val validRange = validOffRentPeriodCalculator.validOffRentTimeRange(offRentDate)
            return validRange.start.roundUpToNearestHour()..validRange.endInclusive
        }

    private val canSelectOffRentTime
        get() = offRentDate != null

    private val canConfirmOffRent
        get() = offRentDate != null && offRentTime != null


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this
    }


    /*---------------------------------------- DataSource ----------------------------------------*/


    override fun validOffRentDateRange(view: OffRentPanelView) = validOffRentPeriod.toLocalDateRange()
    override fun validTimeRangeForOffRentDate(view: OffRentPanelView) = validTimeRangeForOffRentDate
    override fun defaultOffRentDate(view: OffRentPanelView) = defaultOffRentDate
    override fun defaultOffRentTime(view: OffRentPanelView) = defaultOffRentTime
    override fun offRentDate(view: OffRentPanelView) = offRentDate
    override fun offRentTime(view: OffRentPanelView) = offRentTime
    override fun style(view: OffRentPanelView) = style
    override fun country(view: OffRentPanelView) = country
    override fun canSelectOffRentTime(view: OffRentPanelView) = canSelectOffRentTime
    override fun canConfirmOffRent(view: OffRentPanelView) = canConfirmOffRent


    /*----------------------------------------- Delegate -----------------------------------------*/


    override fun onOffRentDatePicked(date: LocalDate) {
        offRentDate = date
        view.notifyDataChanged()
    }

    override fun onOffRentTimePicked(time: LocalTime) {
        offRentTime = time
        view.notifyDataChanged()
    }

    override fun onConfirmSelected() {
        view.navigateBack()
        delegate?.onOffRentDateConfirmed(controller = this, offRentDateTime = offRentDate!!.withTime(offRentTime!!), isAvailableForPickup = this.isAvailableForPickup)
    }

    override fun pickupOptionChanged(location: String) {
        isAvailableForPickup = location
        view.changeMachinePickupLocation()
    }


    /*---------------------------------------- Interfaces ----------------------------------------*/


    interface Delegate {
        fun onOffRentDateConfirmed(controller: OffRentPanelController, offRentDateTime: LocalDateTime, isAvailableForPickup: String?) {}
    }

}