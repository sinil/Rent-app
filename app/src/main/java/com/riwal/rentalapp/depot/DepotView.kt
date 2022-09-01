package com.riwal.rentalapp.depot

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Depot

interface DepotView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()

    interface DataSource {
        fun depots(view: DepotView): List<Depot>
    }

    interface Delegate {
        fun onNavigateBackSelected()
    }

}