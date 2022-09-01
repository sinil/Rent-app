package com.riwal.rentalapp.main.cart

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.AccessoryOrder
import com.riwal.rentalapp.model.MachineOrder

interface CartView : MvcView, ObservableLifecycleView {

    fun notifyCountryChanged()
    var dataSource: DataSource
    var delegate: Delegate


    fun navigateToContact()
    fun navigateToAccessoriesPanel(preparationHandler: ControllerPreparationHandler)
    fun showDeleteMachineOrderConfirmation(machineOrder: MachineOrder)

    interface DataSource {
        fun machineOrders(view: CartView): List<MachineOrder>
        fun canDecreaseQuantity(view: CartView, machineOrder: MachineOrder): Boolean
        fun isAddAccessoriesEnable(view: CartView): Boolean
    }

    interface Delegate {
        fun onDecreaseQuantityButtonClicked(machineOrder: MachineOrder)
        fun onIncreaseQuantityButtonClicked(machineOrder: MachineOrder)
        fun onDeleteMachineOrderButtonClicked(machineOrder: MachineOrder)
        fun onDeleteMachineOrderConfirmed(machineOrder: MachineOrder)
        fun onAddAccessoriesClicked(machineOrder: MachineOrder)
        fun onAccessoryDelete(machineOrder: MachineOrder?, accessory: AccessoryOrder)
        fun onSubmitButtonClicked()
    }

}