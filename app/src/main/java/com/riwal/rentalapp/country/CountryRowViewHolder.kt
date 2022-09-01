package com.riwal.rentalapp.country

import android.view.View
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.Country
import kotlinx.android.synthetic.main.row_country.view.*

class CountryRowViewHolder(itemView: View) : EasyRecyclerView.ViewHolder(itemView) {

    fun updateWith(country: Country, isActive: Boolean) {
        itemView.countryImageView.loadImage(country.picture) // We use loadImage here because some flags are very complex vectors, resulting in performance problems
        itemView.countryTextView.setText(country.localizedNameRes)
        itemView.activeCountryImageView.isVisible = isActive
    }

}