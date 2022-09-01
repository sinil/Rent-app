package com.riwal.rentalapp.model

import android.content.SharedPreferences
import com.riwal.rentalapp.common.extensions.android.get
import com.riwal.rentalapp.common.extensions.android.set
import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.common.extensions.json.fromJson
import com.riwal.rentalapp.common.extensions.json.toJson
import com.riwal.rentalapp.model.Notification.*
import com.riwal.rentalapp.model.api.AcceptTransferMachineRequestBody
import com.riwal.rentalapp.model.api.BackendClient
import com.riwal.rentalapp.model.api.MachineTransferRequestBody
import com.riwal.rentalapp.model.api.RejectTransferMachineRequestBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*

class OrderManager(
        private val sessionManager: SessionManager,
        private val backend: BackendClient,
        private val preferences: SharedPreferences
) {


    /*---------------------------------------- Properties ----------------------------------------*/


    private var observers: List<Observer> = emptyList()

    val user
        get() = sessionManager.user

    var lastOrder = preferences.lastOrder
        private set

    var currentOrder = preferences.currentOrder ?: Order(user?.asContact() ?: lastOrder?.contact)
        private set


    /*----------------------------------------- Lifecycle ----------------------------------------*/


    init {
        EventBus.getDefault().register(this)
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    suspend fun getMachineAccessories(machine: Machine, country: Country): List<Accessory> {
        return backend.getAccessories(machine.rentalType, Locale.getDefault().toLanguageTag(), country.company)
    }

    suspend fun getMyProjects(customerId: String): MyProject? {
        return backend.getMyProject(customerId)
    }

    suspend fun getTransferMachineNotification(CustomerId: String): List<MachineTransferNotification> {
        return backend.getMachineTransferNotification(CustomerId)
    }

    fun addToCurrentOrder(machineOrder: MachineOrder) {
        currentOrder.machineOrders += machineOrder
        save()
        postEvent(MACHINE_ADDED)
    }

    fun removeFromCurrentOrder(machineOrder: MachineOrder) {
        currentOrder.machineOrders -= machineOrder
        save()
        postEvent(MACHINE_REMOVED)
    }

    fun removeAllMachinesFromCurrentOrder() {
        currentOrder.machineOrders = emptyList()
        save()
        postEvent(MACHINE_REMOVED)
    }

    fun addAccessory(machineOrder: MachineOrder, accessoriesOrder: List<AccessoryOrder>) {
        machineOrder.accessories.addAll(accessoriesOrder)
        save()
    }

    fun deleteAccessory(machineOrder: MachineOrder?, accessoryOrder: AccessoryOrder) {
        machineOrder?.accessories?.removeAll { it.accessory.name == accessoryOrder.accessory.name }
        save()

    }

    suspend fun submitCurrentOrder(country: Country) {
        backend.sendOrderRequest(currentOrder, user?.currentCustomer, country)
        lastOrder = currentOrder
        currentOrder = Order(user?.asContact() ?: lastOrder?.contact)
        save()
        postEvent(ORDER_SUBMITTED)
    }

    suspend fun submitMachineTransferRequest(companyId: Int, transferRequestBody: MachineTransferRequestBody, customerId: String) {
       return backend.sendTransferMachineRequest(companyId, transferRequestBody, customerId)
    }

    suspend fun submitRejectTransferRequest(rejectRequestBody: RejectTransferMachineRequestBody) {
        return backend.sendRejectTransferRequest(rejectRequestBody)
    }

    suspend fun submitAcceptTransferRequest(companyId: String, acceptTransferRequestBody: AcceptTransferMachineRequestBody, selectedImageFiles: Array<String>?){
        return backend.sendAcceptMachineTransfer(companyId, acceptTransferRequestBody , selectedImageFiles)
    }

    fun save() {
        preferences.currentOrder = currentOrder
        preferences.lastOrder = lastOrder
    }

    fun addObserver(observer: Observer, transferId: String) {
        observers += observer
            observer.onNotificationStatusChanged(orderManager = this, transferId = transferId)
    }

    fun removeObserver(observer: Observer) {
        observers -= observer
    }


    /*------------------------------------------- Events -----------------------------------------*/


    @Subscribe
    fun onUserChanged(event: SessionManager.UserChangedEvent) {
        val contact = user?.asContact() ?: lastOrder?.contact
        val phoneNumber = contact?.phoneNumber ?: currentOrder.contact?.phoneNumber
        currentOrder.contact = contact?.copy(phoneNumber = phoneNumber)
        save()
    }


    /*-------------------------------------- Private methods -------------------------------------*/


    private var SharedPreferences.lastOrder: Order?
        get() = fromJson(this["lastOrder"])
        set(value) {
            this["lastOrder"] = value.toJson()
        }

    private var SharedPreferences.currentOrder: Order?
        get() = fromJson(this["currentOrder"])
        set(value) {
            this["currentOrder"] = value.toJson()
        }

    /*-------------------------------------- interfaces -------------------------------------*/

    interface Observer {
        fun onNotificationStatusChanged(orderManager: OrderManager, transferId: String)
    }

}