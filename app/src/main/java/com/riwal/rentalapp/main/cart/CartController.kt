package com.riwal.rentalapp.main.cart

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.addaccessories.AddAccessoriesController
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.Notification.MACHINE_CHANGED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CartController(val view: CartView,
                     var activeCountry: Country,
                     val orderManager: OrderManager,
                     val countryManager: CountryManager,
                     val analytics: RentalAnalytics
) : ViewLifecycleObserver, CartView.DataSource, CartView.Delegate, AddAccessoriesController.Delegate,CountryManager.Observer, CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    private lateinit var selectedMachineOrder: MachineOrder


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewCreate() {
        super.onViewCreate()
        countryManager.addObserver(this)


    }
    override fun onViewAppear() {
        super.onViewAppear()

        analytics.userLookingAtCart()
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        countryManager.removeObserver(this)
    }

    /*------------------------------------ Cart View DataSource ----------------------------------*/


    override fun machineOrders(view: CartView) = orderManager.currentOrder.machineOrders
    override fun canDecreaseQuantity(view: CartView, machineOrder: MachineOrder) = machineOrder.quantity > 1
    override fun isAddAccessoriesEnable(view: CartView): Boolean = activeCountry.isAddAccessoriesEnabled


    /*------------------------------------ Cart View Delegate ------------------------------------*/


    override fun onDecreaseQuantityButtonClicked(machineOrder: MachineOrder) {
        machineOrder.quantity -= 1
        orderManager.save()
        postEvent(MACHINE_CHANGED)
        view.notifyDataChanged()
    }

    override fun onIncreaseQuantityButtonClicked(machineOrder: MachineOrder) {
        machineOrder.quantity += 1
        orderManager.save()
        postEvent(MACHINE_CHANGED)
        view.notifyDataChanged()
    }

    override fun onDeleteMachineOrderButtonClicked(machineOrder: MachineOrder) {
        view.showDeleteMachineOrderConfirmation(machineOrder)
    }

    override fun onDeleteMachineOrderConfirmed(machineOrder: MachineOrder) {

        analytics.trackRemoveOrderEvent(machineOrder.machine)
        orderManager.removeFromCurrentOrder(machineOrder)
        view.notifyDataChanged()
    }

    override fun onAddAccessoriesClicked(machineOrder: MachineOrder) {
        selectedMachineOrder = machineOrder
        view.navigateToAccessoriesPanel { destination ->
            val controller = destination as AddAccessoriesController
            controller.delegate = this
            controller.machineOrder = selectedMachineOrder
        }
    }

    override fun onSubmitButtonClicked() {
        analytics.checkoutStart()
        view.navigateToContact()
    }


    override fun onAccessoriesAdded(controller: AddAccessoriesController) {
        view.notifyDataChanged()
    }

    override fun onAccessoryDelete(machineOrder: MachineOrder?, accessory: AccessoryOrder) {

        orderManager.deleteAccessory(machineOrder, accessory)
        view.notifyDataChanged()
    }


    /*---------------------------------- CountryManager Observer ---------------------------------*/


    override fun onCountryChanged(countryManager: CountryManager, country: Country) {
        launch {
            activeCountry = country
            view.notifyCountryChanged()
        }
    }

}
