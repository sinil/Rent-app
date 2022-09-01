package com.riwal.rentalapp.reportbreakdownform

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Rental


interface ReportBreakdownFormView: MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()

    interface DataSource {
        fun rental(view: ReportBreakdownFormView): Rental
        fun canSubmitBreakdown(view: ReportBreakdownFormView): Boolean
        fun contactPhoneNumber(view: ReportBreakdownFormView): String?
    }

    interface Delegate {
        fun onBreakdownMessageChanged(view: ReportBreakdownFormView, breakdownMessage: String)
        fun onBackPressed(view: ReportBreakdownFormView)
        fun onSubmitBreakdownSelected(view: ReportBreakdownFormView, contactPhoneNumber:String?)
    }
}