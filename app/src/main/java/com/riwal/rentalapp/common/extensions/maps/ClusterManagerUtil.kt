package com.riwal.rentalapp.common.extensions.maps

import com.google.maps.android.clustering.ClusterManager

fun <T : com.google.maps.android.clustering.ClusterItem> ClusterManager<T>.setItems(items: Collection<T>) {
    clearItems()
    addItems(items)
    cluster()
}