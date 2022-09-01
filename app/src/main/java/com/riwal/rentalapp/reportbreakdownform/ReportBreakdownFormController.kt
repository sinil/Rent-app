package com.riwal.rentalapp.reportbreakdownform

import com.riwal.rentalapp.model.Rental


class ReportBreakdownFormController(val view: ReportBreakdownFormView) : ReportBreakdownFormView.DataSource, ReportBreakdownFormView.Delegate {


    var delegate: Delegate? = null
    private lateinit var rental: Rental
    private var breakdownMessage = ""

    private val canSubmitBreakdown
        get() = breakdownMessage.isNotEmpty()

    private val contactPhoneNumber
        get() = rental.contact.phoneNumber

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    fun setRental(rental: Rental) {
        this.rental = rental
    }


    /*----------------------------- ReportBreakdownFormView DataSource -----------------------------*/


    override fun rental(view: ReportBreakdownFormView) = rental

    override fun canSubmitBreakdown(view: ReportBreakdownFormView) = canSubmitBreakdown

    override fun contactPhoneNumber(view: ReportBreakdownFormView): String? = contactPhoneNumber

    /*----------------------------- ReportBreakdownFormView Delegate -----------------------------*/


    override fun onBreakdownMessageChanged(view: ReportBreakdownFormView, breakdownMessage: String) {
        this.breakdownMessage = breakdownMessage
        view.notifyDataChanged()
    }


    override fun onBackPressed(view: ReportBreakdownFormView) {
        view.navigateBack()
    }

    override fun onSubmitBreakdownSelected(view: ReportBreakdownFormView, contactPhoneNumber: String?) {
        delegate?.onBreakdownReportFinished(this, breakdownMessage, contactPhoneNumber)
        view.navigateBack()
    }

    /*----------------------------------------- Interfaces ---------------------------------------*/


    interface Delegate {
        fun onBreakdownReportFinished(controller: ReportBreakdownFormController, breakdownMessage: String, contactPhoneNumber: String?)
    }


}