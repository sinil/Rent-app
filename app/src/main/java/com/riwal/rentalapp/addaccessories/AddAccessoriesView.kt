package com.riwal.rentalapp.addaccessories

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Accessory
import com.riwal.rentalapp.model.AccessoryOrder

interface AddAccessoriesView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()

    interface DataSource {
        fun isLoading(view: AddAccessoriesView): Boolean
        fun accessories(view: AddAccessoriesView): List<AccessoryOrder>
    }

    interface Delegate {
        fun onAccessoryQuantitySelected(accessory: Accessory, quantity: Int)
        fun onBackButtonClicked()
        fun onConfirmClicked()
    }
}