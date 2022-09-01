package com.riwal.rentalapp.model

import android.content.SharedPreferences
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.core.random
import com.riwal.rentalapp.common.extensions.json.double
import com.riwal.rentalapp.common.extensions.json.json
import com.riwal.rentalapp.common.extensions.json.list
import com.riwal.rentalapp.common.extensions.json.string

typealias SchemaVersion = Int
typealias SchemaTransformationBlock = () -> Unit

class SharedPreferencesSchemaMigrator {


    /*---------------------------------------- Properties ----------------------------------------*/


    private val minimumSchemaVersion: SchemaVersion = 0
    private val latestSchemaVersion: SchemaVersion = 4


    /*----------------------------------------- Methods ------------------------------------------*/


    fun migratePreferencesIfNeeded(preferences: SharedPreferences) {

        val currentSchemaVersion = preferences["preferencesSchemaVersion"] ?: 0 // No projectSchemaVersion means schema version 0

        if (currentSchemaVersion in minimumSchemaVersion until latestSchemaVersion) {
            migrate(preferences, fromSchemaVersion = currentSchemaVersion, toSchemaVersion = latestSchemaVersion)
        }

    }


    /*------------------------------------- Private methods --------------------------------------*/


    fun migrate(preferences: SharedPreferences, fromSchemaVersion: SchemaVersion, toSchemaVersion: SchemaVersion) {

        for (schemaVersion in (fromSchemaVersion + 1)..toSchemaVersion) {
            cumulativeSchemaTransformations(preferences)[schemaVersion]!!()
        }

        preferences["preferencesSchemaVersion"] = toSchemaVersion
    }

    private fun cumulativeSchemaTransformations(preferences: SharedPreferences): Map<SchemaVersion, SchemaTransformationBlock> = preferences.run {
        mapOf(
                1 to {
                    this["projects"] = jsonList("projects")
                            ?.map { it as Map<String, Any?> }
                            ?.map { migratedProject(it, targetSchemaVersion = 1) }

                    this["LAST_ORDER"] = migratedOrder(jsonObject("LAST_ORDER"), targetSchemaVersion = 1)
                    this["CURRENT_ORDER"] = migratedOrder(jsonObject("CURRENT_ORDER"), targetSchemaVersion = 1)

                    renameKey(from = "USER2", to = "user")
                    renameKey(from = "SELECTED_COUNTRY", to = "selectedCountryCode")
                    renameKey(from = "LAST_ORDER", to = "lastOrder")
                    renameKey(from = "CURRENT_ORDER", to = "currentOrder")
                    renameKey(from = "LATEST_DISPLAYED_APP_VERSION_CODE", to = "lastDisplayedAppVersionCode")
                    renameKey(from = "access4U_token", to = "access4UToken")
                },
                2 to {
                    this["currentOrder"] = migratedOrder(jsonObject("currentOrder"), targetSchemaVersion = 2)
                    this["lastOrder"] = migratedOrder(jsonObject("lastOrder"), targetSchemaVersion = 2)
                },
                3 to {
                    this["user"] = migratedUser(jsonObject("user"), targetSchemaVersion = 3)
                },
                4 to {
                    this["access4URefreshToken"] = jsonObject("user")?.get("refreshToken")
                    this["user"] = migratedUser(jsonObject("user"), targetSchemaVersion = 4)
                }
        )
    }

    private fun migratedUser(user: Map<String, Any?>?, targetSchemaVersion: SchemaVersion) = user?.toMutableMap()?.apply {
        when (targetSchemaVersion) {
            3 -> {
                if (this["currentCompany"] != null) {
                    this["selectedCustomer"] = migratedCustomer(json("currentCompany"), targetSchemaVersion = 3)
                    remove("currentCompany")
                }
                this["customers"] = list("companies")
                        ?.map { it as Map<String, Any> }
                        ?.map { migratedCustomer(it, targetSchemaVersion = 3) }
                remove("companies")
            }
            4 -> {
                remove("refreshToken")
            }
        }
    }

    private fun migratedCustomer(customer: Map<String, Any?>?, targetSchemaVersion: SchemaVersion) = customer?.toMutableMap()?.apply {
        when (targetSchemaVersion) {
            3 -> {
                this["companyId"] = string("id")!!
                this["id"] = string("customerId")!!
                remove("customerId")
            }
        }
    }

    private fun migratedOrder(order: Map<String, Any?>?, targetSchemaVersion: SchemaVersion) = order?.toMutableMap()?.apply {
        when (targetSchemaVersion) {
            1 -> {
                if (json("project") != null) {
                    this["project"] = migratedProject(json("project")!!, targetSchemaVersion = 1)
                }
            }
            2 -> {
                if (list("machineOrders") != null) {
                    this["machineOrders"] = list("machineOrders")
                            ?.map { it as Map<String, Any?> }
                            ?.map { migratedMachineOrder(it, targetSchemaVersion = 2) }!!
                }
            }
        }
    }

    private fun migratedProject(project: Map<String, Any?>?, targetSchemaVersion: SchemaVersion) = project?.toMutableMap()?.apply {
        when (targetSchemaVersion) {
            1 -> {
                this["id"] = Int.random()
                this["coordinate"] = Coordinate(
                        double("latitude")!!,
                        double("longitude")!!
                )
                remove("latitude")
                remove("longitude")
            }
        }
    }

    private fun migratedMachineOrder(machineOrder: Map<String, Any?>?, targetSchemaVersion: SchemaVersion) = machineOrder?.toMutableMap()?.apply {
        when (targetSchemaVersion) {
            2 -> {
                this["offRentTime"] = this["pickupTime"]
                remove("pickupTime")
            }
        }
    }

}
