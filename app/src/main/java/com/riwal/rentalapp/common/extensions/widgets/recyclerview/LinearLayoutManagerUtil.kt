package com.riwal.rentalapp.common.extensions.widgets.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

val LinearLayoutManager.numberOfVisibleItems: Int
    get() {
        val first = findFirstVisibleItemPosition()
        if (first == RecyclerView.NO_POSITION) {
            return 0
        }
        val last = findLastVisibleItemPosition()
        return (first..last).count()
    }