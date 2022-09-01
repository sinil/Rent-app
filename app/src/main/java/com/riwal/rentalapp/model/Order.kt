package com.riwal.rentalapp.model

data class Order(
        var contact: Contact? = null,
        var project: Project? = null,
        var machineOrders: List<MachineOrder> = emptyList(),
        var purchaseOrder: String? = null,
        var notes: String? = null
) {
    fun isEmpty() = totalNumberOfOrderedMachines == 0

    fun isNotEmpty() = !isEmpty()

    val totalNumberOfOrderedMachines
        get() = machineOrders.sumBy { it.quantity }
}