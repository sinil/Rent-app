package com.riwal.rentalapp.controllers

import android.content.res.Resources
import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.core.doInBackground
import com.riwal.rentalapp.main.search.SearchController
import com.riwal.rentalapp.main.search.SearchView
import com.riwal.rentalapp.model.CountryManager
import com.riwal.rentalapp.model.Machine
import com.riwal.rentalapp.model.Machine.Propulsion
import com.riwal.rentalapp.model.Machine.Propulsion.*
import com.riwal.rentalapp.model.MachinesManager
import com.riwal.rentalapp.relaxedMockk
import io.kotlintest.Description
import io.kotlintest.specs.StringSpec
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

class SearchControllerTests : StringSpec() {

    private val testMachines: List<Machine> = listOf(
            createMachine(ELECTRIC),
            createMachine(ELECTRIC),
            createMachine(HYBRID),
            createMachine(HYBRID_GASOLINE),
            createMachine(HYBRID_DIESEL),
            createMachine(DIESEL),
            createMachine(NATURAL_GAS),
            createMachine(GASOLINE),
            createMachine(UNKNOWN)
    )

    override fun beforeTest(description: Description) {
        super.beforeTest(description)

        val slot = CapturingSlot<suspend CoroutineScope.() -> Unit>()
        mockkStatic("com.riwal.rentalapp.common.util.BuildersUtilKt")
        every { doInBackground(block = capture(slot)) } answers { runBlocking(block = slot.captured) }
    }

    init {

//        "Given a set of machines and an empty filter, when filtering machines, then the result should contain all machines" {
//
//            // Given
//            val filter = Filter()
//            val searchController = controller()
//            every { searchController.machinesManager.machines } returns testMachines
//            every { searchController.machinesManager.machinesForCountry(any()) } returns testMachines
//
//            // when
//            searchController.preFilterMachinesOnCountryAndNotifyView()
//            searchController.onFilterChanged(filter)
//
//            // then
//            val filteredMachines = searchController.searchResults(view = mockk())
//            filteredMachines shouldBe testMachines
//        }
//
//        "When electric propulsion filter has been set, should return only machines with electric propulsion" {
//
//            // Given
//            val filter = Filter(electricPropulsion = true)
//            val searchController = controller()
//            every { searchController.machinesManager.machines } returns testMachines
//            every { searchController.machinesManager.machinesForCountry(any()) } returns testMachines
//
//            // When
//            searchController.preFilterMachinesOnCountryAndNotifyView()
//            searchController.onFilterChanged(filter = filter)
//
//            // Then
//            val filteredMachines = searchController.searchResults(view = mockk())
//            filteredMachines shouldBe testMachines.filter { it.propulsion == ELECTRIC }
//
//        }
//
//        "When fossil fuel propulsion filter has been set, should return only machines with fossil fuel propulsion" {
//
//            // Given
//            val filter = Filter(fossilFuelPropulsion = true)
//            val searchController = controller()
//            every { searchController.machinesManager.machines } returns testMachines
//            every { searchController.machinesManager.machinesForCountry(any()) } returns testMachines
//
//            // When
//            searchController.preFilterMachinesOnCountryAndNotifyView()
//            searchController.onFilterChanged(filter = filter)
//
//            // Then
//            val filteredMachines = searchController.searchResults(view = mockk())
//            filteredMachines shouldBe testMachines.filter { it.propulsion in listOf(DIESEL, NATURAL_GAS, GASOLINE) }
//        }
//
//        "When hybrid propulsion filter has been set, should return only machines with hybrid propulsion" {
//
//            // Given
//            val filter = Filter(hybridPropulsion = true)
//            val searchController = controller()
//            every { searchController.machinesManager.machines } returns testMachines
//            every { searchController.machinesManager.machinesForCountry(any()) } returns testMachines
//
//            // when
//            searchController.preFilterMachinesOnCountryAndNotifyView()
//            searchController.onFilterChanged(filter = filter)
//
//            // then
//            val filteredMachines = searchController.searchResults(view = mockk())
//            filteredMachines shouldBe testMachines.filter { it.propulsion in listOf(HYBRID, HYBRID_GASOLINE, HYBRID_DIESEL) }
//        }
//
//        "When fossil fuel and electric propulsion filter has been set, should return only machines with diesel and electric propulsion" {
//
//            // Given
//            val filter = Filter(electricPropulsion = true, fossilFuelPropulsion = true)
//            val searchController = controller()
//            every { searchController.machinesManager.machines } returns testMachines
//            every { searchController.machinesManager.machinesForCountry(any()) } returns testMachines
//
//            // When
//            searchController.preFilterMachinesOnCountryAndNotifyView()
//            searchController.onFilterChanged(filter = filter)
//
//            // Then
//            val filteredMachines = searchController.searchResults(view = mockk())
//            filteredMachines shouldBe testMachines.filter { it.propulsion in listOf(ELECTRIC, DIESEL, NATURAL_GAS, GASOLINE) }
//        }
//
//        "When fossil fuel, electric and hybrid propulsion filter has been set, should return only machines with diesel and electric propulsion" {
//
//            // Given
//            val filter = Filter(electricPropulsion = true, fossilFuelPropulsion = true, hybridPropulsion = true)
//            val searchController = controller()
//            every { searchController.machinesManager.machines } returns testMachines
//            every { searchController.machinesManager.machinesForCountry(any()) } returns testMachines
//
//            // When
//            searchController.preFilterMachinesOnCountryAndNotifyView()
//            searchController.onFilterChanged(filter = filter)
//
//            // Then
//            val filteredMachines = searchController.searchResults(view = mockk())
//            filteredMachines shouldBe testMachines.filter { it.propulsion in listOf(ELECTRIC, HYBRID, HYBRID_DIESEL, HYBRID_GASOLINE, DIESEL, NATURAL_GAS, GASOLINE) }
//        }
    }

    private fun controller(view: SearchView = relaxedMockk(),
                           machinesManager: MachinesManager = relaxedMockk(),
                           countryManager: CountryManager = relaxedMockk(),
                           resources: Resources = mockk(),
                           analytics: RentalAnalytics = mockk()) = SearchController(view, machinesManager, countryManager, resources, analytics)

    private fun createMachine(propulsion: Propulsion): Machine = relaxedMockk {
        every { this@relaxedMockk.propulsion } returns propulsion
    }
}