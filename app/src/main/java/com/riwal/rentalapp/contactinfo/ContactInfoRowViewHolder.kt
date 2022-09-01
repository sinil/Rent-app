package com.riwal.rentalapp.contactinfo

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.extensions.widgets.recyclerview.context
import com.riwal.rentalapp.model.ContactInfo
import kotlinx.android.synthetic.main.row_contact_info.view.*

class ContactInfoRowViewHolder(itemView: View) : EasyRecyclerView.ViewHolder(itemView) {

    fun updateWith(contactInfo: ContactInfo) {
        itemView.contactInfoNameTextView.text = contactInfo.name

        itemView.contactInfoPhoneTextView.text = contactInfo.phoneNumber
        itemView.contactInfoPhoneTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contactInfo.phoneNumber}"))
            context.startActivity(intent)
        }

        val hasEmail = contactInfo.email != null
        itemView.contactInfoMaiTextView.text = contactInfo.email
        itemView.contactInfoMaiTextView.isVisible = hasEmail
        itemView.contactInfoMaiTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${contactInfo.email}"))
            context.startActivity(intent)
        }
    }

}