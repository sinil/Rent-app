package com.riwal.rentalapp.common.ui

import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker
import com.riwal.rentalapp.R
import kotlinx.android.synthetic.main.view_map_info.view.*

internal class MapInfoWindowAdapter(val inflater: LayoutInflater) : InfoWindowAdapter {

    private val popup: View by lazy<View> {
        inflater.inflate(R.layout.view_map_info, null)
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {

        popup.titleTextView.text = marker.title
        popup.subtitleTextView.text = marker.snippet

        return popup
    }
}