package com.riwal.rentalapp.model

import android.content.Context
import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.riwal.rentalapp.R
import java.util.*

data class Machine(
        val id: String,
        val rentalType: String,
        val brand: String,
        val model: String,
        val type: Type,
        val weight: Float,
        val width: Float,
        val length: Float,
        val height: Float,
        val platformLength: Float,
        val platformWidth: Float,
        val liftCapacity: Float,
        val propulsion: Propulsion,
        val workingHeight: Float,
        val workingOutreach: Float,
        val countries: List<Country>,
        val images: List<String>,
        val thumbnailUrl: String?,
        val diagram: String?,
        val documents: List<Document>,
        val meshes: List<MachineMeshDescriptor>) {

    val canRunOnElectricity
        get() = propulsion in Propulsion.electricPropulsions || propulsion in Propulsion.hybridPropulsions

    val canRunOnFossilFuel
        get() = propulsion in Propulsion.fossilFuelPropulsions || propulsion in Propulsion.hybridPropulsions

    enum class Type(@DrawableRes val imageRes: Int, @StringRes val localizedNameRes: Int, @StringRes val descriptionRes: Int) {
        SCISSOR_LIFT(R.drawable.img_scissor_lift, R.string.scissor_lift, R.string.scissor_lift_description),
        VERTICAL_LIFT(R.drawable.img_vertical_lift, R.string.vertical_lift, R.string.vertical_lift_description),
        CRAWLER_LIFT(R.drawable.img_crawler_lift, R.string.crawler_lift, R.string.crawler_lift_description),
        ARTICULATED_BOOM_LIFT(R.drawable.img_articulated_boom_lift, R.string.articulated_boom_lift, R.string.articulated_boom_lift_description),
        SPIDER_LIFT(R.drawable.img_spider_lift, R.string.spider_lift, R.string.spider_lift_description),
        TELESCOPIC_BOOM_LIFT(R.drawable.img_telescopic_boom_lift, R.string.telescopic_boom_lift, R.string.telescopic_boom_lift_description),
        TRAILER_MOUNTED_LIFT(R.drawable.img_trailer_mounted_lift, R.string.trailer_mounted_lift, R.string.trailer_mounted_lift_description),
        TRUCK_MOUNTED_LIFT(R.drawable.img_truck_mounted_lift, R.string.truck_mounted_lift, R.string.truck_mounted_lift_description),
        FORKLIFT(R.drawable.img_forklift, R.string.forklift, R.string.forklift_description),
        TELEHANDLER_STANDARD(R.drawable.img_telehandler_standard, R.string.telehandler_standard, R.string.telehandler_standard_description),
        TELEHANDLER_ROTATING(R.drawable.img_telehandler_rotating, R.string.telehandler_rotating, R.string.telehandler_rotating_description),
        CRANE(R.drawable.img_crane, R.string.crane, R.string.crane_description),
        GLASS_LIFT(R.drawable.img_glass_lift, R.string.glass_lift, R.string.glass_lift_description)
    }

    enum class Propulsion(@StringRes val localizedNameRes: Int) {
        UNKNOWN(R.string.unknown),
        ELECTRIC(R.string.electric),
        DIESEL(R.string.diesel),
        HYBRID_GASOLINE(R.string.hybrid_gasoline),
        HYBRID_DIESEL(R.string.hybrid_diesel),
        HYBRID(R.string.hybrid),
        GASOLINE(R.string.gasoline),
        NATURAL_GAS(R.string.natural_gas);

        companion object {
            val electricPropulsions
                get() = listOf(ELECTRIC)

            val fossilFuelPropulsions
                get() = listOf(DIESEL, GASOLINE, NATURAL_GAS)

            val hybridPropulsions
                get() = listOf(HYBRID, HYBRID_DIESEL, HYBRID_GASOLINE)

            val allPropulsions
                get() = values().toList()
        }
    }
}

fun Machine.searchableType(resources: Resources): String = resources.getString(type.localizedNameRes)
fun Machine.searchablePropulsion(resources: Resources): String = resources.getString(propulsion.localizedNameRes)
fun Machine.searchableProperties(resources: Resources) = "$rentalType$brand$model ${searchableType(resources).replace(" ", "")} ${searchablePropulsion(resources)}"

fun Machine.localizedMachineType(context: Context): String = context.getString(type.localizedNameRes)

data class MachineMeshDescriptor(val company: Company, val url: String, val position: MachineMeshPosition)

enum class MachineMeshPosition {
    UNKNOWN,
    STOWED_POSITION,
    HORIZONTAL_OUTREACH,
    VERTICAL_OUTREACH,
    ELEVATED_POSITION,
    CLOSED_POSITION,
    TRANSPORTATION_POSITION
}