package com.riwal.rentalapp.main.search

import android.content.res.Resources
import com.riwal.rentalapp.model.Machine
import com.riwal.rentalapp.model.Machine.Propulsion
import com.riwal.rentalapp.model.searchableProperties

data class MachinesFilter(
        val query: String = "",
        val minimumWorkingHeight: Int = 0,
        val minimumWorkingOutreach: Int = 0,
        val minimumLiftCapacity: Int = 0,
        val electricPropulsion: Boolean = false,
        val hybridPropulsion: Boolean = false,
        val fossilFuelPropulsion: Boolean = false,
        var machineType: Machine.Type? = null
) {

    private val propulsionFilters: List<Propulsion>
        get() {
            val electric = if (electricPropulsion) Propulsion.electricPropulsions else emptyList()
            val fossilFuel = if (fossilFuelPropulsion) Propulsion.fossilFuelPropulsions else emptyList()
            val hybrid = if (hybridPropulsion) Propulsion.hybridPropulsions else emptyList()
            return electric + fossilFuel + hybrid
        }

    fun applyTo(input: List<Machine>, resources: Resources) = input.asSequence()
            .filter { it.workingHeight >= minimumWorkingHeight }
            .filter { it.workingOutreach >= minimumWorkingOutreach }
            .filter { it.liftCapacity >= minimumLiftCapacity }
            .filter { propulsionFilters.isEmpty() || it.propulsion in propulsionFilters }
            .filter { machine ->
                val subQueries = query.split("|")
                subQueries.isEmpty() || subQueries.any { subQuery ->
                    val searchTerms = subQuery.split(",", " ").filter { it.isNotBlank() }
                    searchTerms.all { searchTerm ->
                        machine.searchableProperties(resources).contains(searchTerm, ignoreCase = true)
                    }
                }
            }
            .filter { machineType == null || it.type.localizedNameRes == machineType?.localizedNameRes }
            .sortedBy { it.workingHeight }
            .toList()

}