package com.riwal.rentalapp.orderpanel

import com.riwal.rentalapp.common.extensions.datetime.toLocalDateRange
import com.riwal.rentalapp.common.extensions.datetime.toLocalTime
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.MachineOrder.OffRentDateStrictness
import com.riwal.rentalapp.model.MachineOrder.OffRentDateStrictness.EXACT
import com.riwal.rentalapp.model.MachineOrder.OffRentDateStrictness.UNSPECIFIED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.joda.time.LocalDate
import org.joda.time.LocalTime


class OrderPanelController(val view: OrderPanelView, val country: Country, val orderManager: OrderManager) : OrderPanelView.DataSource, OrderPanelView.Delegate, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/


    var delegate: Delegate? = null
    lateinit var machine: Machine

    private val validOnRentPeriodCalculator = ValidOnRentPeriodCalculator(nextDayDeliveryCutoffTime = country.nextDayDeliveryCutoffTime, weekend = country.weekend)
    private val validOffRentPeriodCalculator = ValidOffRentPeriodCalculator(sameDayOffRentCutoffTime = country.sameDayOffRentCutoffTime)

    private var onRentDate: LocalDate? = null
    private var onRentTime: LocalTime? = null
    private var offRentDate: LocalDate? = null
    private var offRentTime: LocalTime? = null
    private var offRentDateStrictness = UNSPECIFIED

    private val defaultOnRentDate
        get() = validOnRentPeriod.start.toLocalDate()!!

    private val defaultOnRentTime
        get() = "08:00".toLocalTime()!!

    private val defaultOffRentDate
        get() = validOffRentPeriod.start.toLocalDate()!!

    private val defaultOffRentTime
        get() = "08:00".toLocalTime()!!

    private val validOnRentPeriod
        get() = validOnRentPeriodCalculator.validOnRentPeriod(offRentDate, offRentTime)

    private val validTimeRangeForOnRentDate
        get() = validOnRentPeriodCalculator.validOnRentTimeRange(onRentDate, offRentDate, offRentTime)

    private val validOffRentPeriod
        get() = validOffRentPeriodCalculator.validOffRentPeriod(onRentDate, onRentTime)

    private val validTimeRangeForOffRentDate
        get() = validOffRentPeriodCalculator.validOffRentTimeRange(offRentDate, onRentDate, onRentTime)

    private val canSelectOnRentTime
        get() = onRentDate != null

    private val canSelectOffRentStrictness
        get() = onRentDate != null && onRentTime != null

    private val canSelectOffRentDate
        get() = offRentDateStrictness != UNSPECIFIED

    private val canSelectOffRentTime
        get() = offRentDate != null

    private val canAddToOrder
        get() = onRentDate != null && onRentTime != null && offRentDate != null && (offRentDateStrictness != EXACT || offRentTime != null)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this
    }


    /*---------------------------------------- DataSource ----------------------------------------*/


    override fun validOnRentDateRange(view: OrderPanelView) = validOnRentPeriod.toLocalDateRange()
    override fun validOffRentDateRange(view: OrderPanelView) = validOffRentPeriod.toLocalDateRange()
    override fun validTimeRangeForOnRentDate(view: OrderPanelView) = validTimeRangeForOnRentDate
    override fun validTimeRangeForOffRentDate(view: OrderPanelView) = validTimeRangeForOffRentDate
    override fun defaultOnRentDate(view: OrderPanelView) = defaultOnRentDate
    override fun defaultOnRentTime(view: OrderPanelView) = defaultOnRentTime
    override fun defaultOffRentDate(view: OrderPanelView) = defaultOffRentDate
    override fun defaultOffRentTime(view: OrderPanelView) = defaultOffRentTime
    override fun onRentDate(view: OrderPanelView) = onRentDate
    override fun onRentTime(view: OrderPanelView) = onRentTime
    override fun offRentDateStrictness(view: OrderPanelView) = offRentDateStrictness
    override fun offRentDate(view: OrderPanelView) = offRentDate
    override fun offRentTime(view: OrderPanelView) = offRentTime
    override fun canSelectOnRentTime(view: OrderPanelView) = canSelectOnRentTime
    override fun canSelectOffRentStrictness(view: OrderPanelView) = canSelectOffRentStrictness
    override fun canSelectOffRentDate(view: OrderPanelView) = canSelectOffRentDate
    override fun canSelectOffRentTime(view: OrderPanelView) = canSelectOffRentTime
    override fun canAddToOrder(view: OrderPanelView) = canAddToOrder
    override fun country(view: OrderPanelView) = country


    /*----------------------------------------- Delegate -----------------------------------------*/


    override fun onOnRentDatePicked(date: LocalDate) {

        if (DayOfWeek.fromValue(date.dayOfWeek) in country.weekend && country.name != "DK") {
            view.notifyCannotStartRentInWeekend()
        } else {
            onRentDate = date
            view.notifyDataChanged()
        }
    }

    override fun onOnRentTimePicked(time: LocalTime) {
        onRentTime = time
        view.notifyDataChanged()
    }

    override fun onOffRentCertaintySelected(strictness: OffRentDateStrictness) {
        offRentDateStrictness = strictness
        view.notifyDataChanged()
    }

    override fun onOffRentDatePicked(date: LocalDate) {
        offRentDate = date
        view.notifyDataChanged()
    }

    override fun onOffRentTimePicked(time: LocalTime) {
        offRentTime = time
        view.notifyDataChanged()
    }

    override fun onConfirmSelected() {
        val machineOrder = MachineOrder(machine, 1, onRentDate!!, onRentTime!!, offRentDate!!, offRentTime, offRentDateStrictness)
        view.navigateBack()
        delegate?.onOrderFinished(controller = this, machineOrder = machineOrder)
    }


    /*---------------------------------------- Interfaces ----------------------------------------*/


    interface Delegate {
        fun onOrderFinished(controller: OrderPanelController, machineOrder: MachineOrder) {}
    }

}