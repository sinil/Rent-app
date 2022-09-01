package com.riwal.rentalapp.helpmechoose

import android.content.res.Resources
import com.riwal.rentalapp.main.search.MachinesFilter
import com.riwal.rentalapp.model.Machine
import com.riwal.rentalapp.model.Machine.Type.*

data class WorkSpecification(
        val liftType: LiftType = LiftType.NOT_SPECIFIED,
        val location: Location = Location.NOT_SPECIFIED,
        val reachType: ReachType = ReachType.NOT_SPECIFIED,
        val minimumWorkingHeight: Int = 0,
        val minimumWorkingOutreach: Int = 0
) {


    /*--------------------------------------- Properties -----------------------------------------*/


    val matchingMachineTypes
        get() = recommendedMachineTypesForLiftType(liftType) intersect
                recommendedMachineTypesForLocation(location) intersect
                recommendedMachineTypesForReachType(reachType)


    /*----------------------------------------- Methods ------------------------------------------*/


    fun toMachinesFilter(availableMachines: List<Machine.Type>, resources: Resources) = MachinesFilter(
            query = (matchingMachineTypes intersect availableMachines)
                    .joinToString(separator = " | ") { resources.getString(it.localizedNameRes) },
            minimumWorkingHeight = minimumWorkingHeight,
            minimumWorkingOutreach = minimumWorkingOutreach,
            electricPropulsion = true,
            hybridPropulsion = true,
            fossilFuelPropulsion = location != Location.INDOORS
    )


    /*-------------------------------------- Private methods -------------------------------------*/


    private fun recommendedMachineTypesForLiftType(liftType: LiftType) = when (liftType) {
        LiftType.PEOPLE -> listOf(SCISSOR_LIFT, VERTICAL_LIFT, TELESCOPIC_BOOM_LIFT, TRUCK_MOUNTED_LIFT, ARTICULATED_BOOM_LIFT, SPIDER_LIFT, TRAILER_MOUNTED_LIFT)
        LiftType.MATERIALS -> listOf(FORKLIFT, TELEHANDLER_STANDARD, TELEHANDLER_ROTATING, GLASS_LIFT, CRANE)
        LiftType.NOT_SPECIFIED -> allMachineTypes
    }

    private fun recommendedMachineTypesForLocation(location: Location) = when (location) {
        Location.OUTDOORS -> allMachineTypes
        Location.INDOORS -> listOf(SCISSOR_LIFT, VERTICAL_LIFT, TELESCOPIC_BOOM_LIFT, ARTICULATED_BOOM_LIFT, SPIDER_LIFT, FORKLIFT, GLASS_LIFT) // Some big requestedMachine types or truck/trailer mounted types are not recommended for indoor use
        Location.NOT_SPECIFIED -> allMachineTypes
    }

    private fun recommendedMachineTypesForReachType(reachType: ReachType) = when (reachType) {
        ReachType.VERTICAL -> listOf(SCISSOR_LIFT, VERTICAL_LIFT)
        ReachType.ANGLED -> listOf(TELESCOPIC_BOOM_LIFT, TRUCK_MOUNTED_LIFT)
        ReachType.MULTI_ANGLED -> listOf(ARTICULATED_BOOM_LIFT, SPIDER_LIFT, TRAILER_MOUNTED_LIFT)
        ReachType.NOT_SPECIFIED -> allMachineTypes
    }

    companion object {

        private val allMachineTypes = Machine.Type.values().toList()

    }
}