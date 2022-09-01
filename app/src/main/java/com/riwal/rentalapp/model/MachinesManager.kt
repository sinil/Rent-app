package com.riwal.rentalapp.model

import android.annotation.SuppressLint
import com.riwal.rentalapp.common.extensions.core.pathExtension
import com.riwal.rentalapp.model.api.BackendClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MachinesManager(
        val backendClient: BackendClient,
        val company: Company,
        val countryManager: CountryManager
) {


    /*--------------------------------------- Properties -----------------------------------------*/


    private var observers: List<Observer> = emptyList()

    var machines: List<Machine> = emptyList()
        private set


    /*---------------------------------------- Methods -------------------------------------------*/


    @SuppressLint("CheckResult")
    fun updateMachinesIfNeeded(): List<Machine> {

        // When the app is kept in RAM for a long time, the machines won't be updated anymore.
        // TODO: Add a scheduler to update the machines once a day.
//        if (machines.isNotEmpty()) {
//            return
//        }

        GlobalScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    machines = backendClient.machines(countryManager.activeCountry!!)
                }
                machines = machines.map { it.withIrrelevantMeshesRemoved() }
                notifyObservers(machines)
            } catch (error: Exception) {
                notifyObservers(emptyList(), error)
            }
        }
        return machines
    }

//    fun machinesForCountry(country: Country) = machines.filter { country in it.countries }

    fun machineTypesForCountry(country: Country) = machines
            .asSequence()
            .map { it.type }
            .distinct()
            .toList()

    // TODO: For French machines, the rentalType is in the brand field. Once this is fixed, the part after the || can be removed
    fun machineForRental(rental: Rental): Machine? =
            machines.firstOrNull { it.rentalType == rental.rentalType || it.rentalType == rental.brand || it.rentalType == rental.itemName }


    // TODO: Update the requested Item brand in the API
    fun machineForRental(rentalDetail: RentalDetail) = machines.firstOrNull { it.rentalType == rentalDetail.itemRequested || it.rentalType == rentalDetail.brand }

    fun machineImage(rentalType: String?) = machines.firstOrNull { it.rentalType == rentalType }?.thumbnailUrl

    fun addObserver(observer: Observer) {
        observers += observer
        if (machines.isNotEmpty()) {
            observer.onMachinesUpdated(machinesManager = this, machines = machines)
        }
    }

    fun removeObserver(observer: Observer) {
        observers -= observer
    }


    /*------------------------------------ Private methods ---------------------------------------*/


    private fun notifyObservers(machines: List<Machine>, error: Exception? = null) {
        observers.forEach {
            if (error == null) {
                it.onMachinesUpdated(machinesManager = this, machines = machines)
            } else {
                it.onMachinesUpdateFailed(machinesManager = this, error = error)
            }
        }
    }

    private fun Machine.withIrrelevantMeshesRemoved(): Machine {
        val relevantMeshes = meshes
                .filter { it.url.pathExtension() == ".glb" }
                .filter { it.company == company }
        return this.copy(meshes = relevantMeshes)
    }


    /*--------------------------------------- Interfaces -----------------------------------------*/


    interface Observer {
        fun onMachinesUpdated(machinesManager: MachinesManager, machines: List<Machine>)
        fun onMachinesUpdateFailed(machinesManager: MachinesManager, error: Exception) {}
    }

}