package com.riwal.rentalapp.depot

import android.content.Intent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.context
import com.riwal.rentalapp.common.extensions.core.toUri
import com.riwal.rentalapp.model.Depot
import kotlinx.android.synthetic.main.row_depot.view.*

class DepotRowViewHolder(itemView: View) : EasyRecyclerView.ViewHolder(itemView) {

    fun updateWith(depot: Depot) {
        itemView.depotNameTextView.text = depot.name

        itemView.depotAddressTextView.text = depot.address
        itemView.depotAddressTextView.setOnClickListener {
            val geoUriString = depot.coordinate?.run { "geo:0,0?q=$latitude,$longitude" } ?: "geo:0,0?q=${depot.address}"
            val intent = Intent(Intent.ACTION_VIEW, geoUriString.toUri())
            context.startActivity(intent)
        }

        val hasPhoneNumber = depot.phoneNumber != null
        itemView.depotPhoneTextView.text = depot.phoneNumber
        itemView.depotPhoneTextView.isVisible = hasPhoneNumber
        itemView.depotPhoneTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, "tel:${depot.phoneNumber}".toUri())
            context.startActivity(intent)
        }

        val hasEmail = depot.email != null
        itemView.depotMailTextView.text = depot.email
        itemView.depotMailTextView.isVisible = hasEmail
        itemView.depotMailTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, "mailto:${depot.email}".toUri())
            context.startActivity(intent)
        }
    }

}