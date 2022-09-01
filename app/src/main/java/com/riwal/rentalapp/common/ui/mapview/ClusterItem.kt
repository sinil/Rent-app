package com.riwal.rentalapp.common.ui.mapview

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster

open class ClusterItem(title: String, subtitle: String? = null, position: LatLng) : com.google.maps.android.clustering.ClusterItem {

    private val _title = title
    private val _subtitle = subtitle
    private val _position = position

    override fun getTitle() = _title
    override fun getSnippet() = _subtitle
    override fun getPosition() = _position

}

val Cluster<ClusterItem>.memberItems
    get() = items.filterIsInstance<ClusterItem>()
