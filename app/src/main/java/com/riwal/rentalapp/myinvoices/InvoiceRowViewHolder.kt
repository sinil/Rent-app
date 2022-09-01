package com.riwal.rentalapp.myinvoices

import android.annotation.SuppressLint
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.core.formatThousandSeparator
import com.riwal.rentalapp.common.extensions.datetime.UTC_DATE_FORMATTER
import com.riwal.rentalapp.common.extensions.datetime.toMediumStyleString
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.context
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.getString
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.Invoice
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_invoice.*
import org.joda.time.LocalDate


class InvoiceRowViewHolder(override val containerView: View, val delegate: MyInvoicesView.Delegate) : EasyRecyclerView.ViewHolder(containerView), LayoutContainer {

    @SuppressLint("SetTextI18n")
    fun updateWith(invoice: Invoice) {

        val invoiceNumber = invoice.invoiceNumber
        val invoiceDate = invoice.invoiceDate
        val invoiceDueDate = invoice.dueDate
        val invoiceType = invoice.invoiceType
        val orderNumber = invoice.orderNumber
        val purchaseOrder = invoice.purchaseOrder
        val amount = invoice.amount
        val paid = invoice.paid
        val overDue = invoice.overDue


        invoiceNumberTextView.text = invoiceNumber
        invoiceTypeValueTextView.text = getString(invoiceType?.localizedNameRes)
        rentalOrderNumberValueTextView.text = orderNumber
        purchaseOrderValueTextView.text = purchaseOrder
        amountValueTextView.text = amount.formatThousandSeparator()


        paidValueTextView.text = if (paid) getString(R.string.yes) else getString(R.string.no)
        overdueValueTextView.text = getString(R.string.invoice_due_days, overDue.toString())

        invDateValueTextView.text = LocalDate.parse(invoiceDate, UTC_DATE_FORMATTER).toMediumStyleString(context)
        dueDateValueTextView.text = LocalDate.parse(invoiceDueDate, UTC_DATE_FORMATTER).toMediumStyleString(context)


        downloadPdfImageView.setOnClickListener {
            delegate.onDownloadInvoiceSelected(context, invoice)
        }

    }

}