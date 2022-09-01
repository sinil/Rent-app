package com.riwal.rentalapp.myquotations

import android.view.View
import com.riwal.rentalapp.common.extensions.datetime.UTC_DATE_FORMATTER
import com.riwal.rentalapp.common.extensions.datetime.toMediumStyleString
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.context
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.Quotation
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_my_quotations.*
import org.joda.time.LocalDate

class MyQuotationsViewHolder (override val containerView: View, val delegate: MyQuotationsView.Delegate) : EasyRecyclerView.ViewHolder(containerView), LayoutContainer {


    fun updateWith(quotation: Quotation){

        quotationNumberTextView.text = quotation.quotationNo
        quotationDateTextView.text = quotation.quotationDate?.toLocalDate()?.toMediumStyleString(context)
        onHireDateTextView.text = quotation.onHireDate?.toLocalDate()?.toMediumStyleString(context)
        offHireDateTextView.text = quotation.offHireDate?.toLocalDate()?.toMediumStyleString(context)
        venueCityTextView.text = quotation.venueCity
        contactPersonTextView.text = quotation.contactPerson
        amountQuotationTextView.text = quotation.quotationTotal

        downloadPdfImageView.setOnClickListener {
            delegate.onDownloadQuotationSelected(context, quotation)
        }

    }

}