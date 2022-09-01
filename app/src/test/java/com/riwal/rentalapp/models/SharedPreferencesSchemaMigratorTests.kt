package com.riwal.rentalapp.models

import android.content.SharedPreferences
import com.riwal.rentalapp.FakePreferences
import com.riwal.rentalapp.common.extensions.android.get
import com.riwal.rentalapp.common.extensions.android.plusAssign
import com.riwal.rentalapp.common.extensions.android.set
import com.riwal.rentalapp.common.extensions.android.toJsonObject
import com.riwal.rentalapp.common.extensions.core.random
import com.riwal.rentalapp.common.extensions.json.toJsonObject
import com.riwal.rentalapp.model.SharedPreferencesSchemaMigrator
import io.kotlintest.Description
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockkStatic

class SharedPreferencesSchemaMigratorTests : StringSpec() {


    override fun beforeTest(description: Description) {
        super.beforeTest(description)

        mockkStatic("com.riwal.rentalapp.common.util.NumberUtilKt")
        every { Int.random() } answers { 0 }
    }


    init {


        "0 to 1" {

            // Given
            val preferencesMigrator = SharedPreferencesSchemaMigrator()
            val preferences = preferences()

            val oldProject = mapOf("latitude" to 123.0, "longitude" to 321.0)
            val newProject = mapOf("id" to 0.0, "coordinate" to mapOf("latitude" to 123.0, "longitude" to 321.0))

            val oldOrder = mapOf("project" to oldProject)
            val newOrder = mapOf("project" to newProject)

            val any = mapOf("any" to "any")

            preferences["projects"] = listOf(oldProject)
            preferences["LAST_ORDER"] = oldOrder
            preferences["CURRENT_ORDER"] = oldOrder
            preferences["USER2"] = any
            preferences["SELECTED_COUNTRY"] = any
            preferences["LATEST_DISPLAYED_APP_VERSION_CODE"] = any
            preferences["access4U_token"] = any

            // When
            preferencesMigrator.migrate(preferences, fromSchemaVersion = 0, toSchemaVersion = 1)

            // Then
            preferences.get<Any>("projects") shouldBe listOf(newProject)

            preferences.get<Any>("USER2") shouldBe null
            preferences.get<Any>("user") shouldBe any

            preferences.get<Any>("SELECTED_COUNTRY") shouldBe null
            preferences.get<Any>("user") shouldBe any

            preferences.get<Any>("LAST_ORDER") shouldBe null
            preferences.get<Any>("lastOrder") shouldBe newOrder

            preferences.get<Any>("CURRENT_ORDER") shouldBe null
            preferences.get<Any>("currentOrder") shouldBe newOrder

            preferences.get<Any>("LATEST_DISPLAYED_APP_VERSION_CODE") shouldBe null
            preferences.get<Any>("lastDisplayedAppVersionCode") shouldBe any

            preferences.get<Any>("access4U_token") shouldBe null
            preferences.get<Any>("access4UToken") shouldBe any

        }

        "1 to 2" {

            // Given
            val time = "9:03:24.452"

            val preferencesMigrator = SharedPreferencesSchemaMigrator()
            val preferences = preferences()

            val oldMachineOrder = mapOf("pickupTime" to time)
            val newMachineOrder = mapOf("offRentTime" to time)
            val orderWithOldMachineOrders = mapOf("machineOrders" to listOf(oldMachineOrder))
            val orderWithNewMachineOrders = mapOf("machineOrders" to listOf(newMachineOrder))

            preferences["currentOrder"] = orderWithOldMachineOrders
            preferences["lastOrder"] = orderWithOldMachineOrders

            // When
            preferencesMigrator.migrate(preferences, fromSchemaVersion = 1, toSchemaVersion = 2)

            // Then
            preferences.get<Any>("currentOrder") shouldBe orderWithNewMachineOrders
            preferences.get<Any>("lastOrder") shouldBe orderWithNewMachineOrders

        }

        "2 to 3" {

            // Given

            val preferencesMigrator = SharedPreferencesSchemaMigrator()
            val preferences = preferences()

            val company = mapOf(
                    "id" to "CompanyId",
                    "customerId" to "CustomerId"
            )
            val customer = mapOf(
                    "id" to "CustomerId",
                    "companyId" to "CompanyId"
            )

            val companies = listOf(company)
            val customers = listOf(customer)

            val oldUser = mapOf(
                    "currentCompany" to company,
                    "companies" to companies
            )
            val newUser = mapOf(
                    "selectedCustomer" to customer,
                    "customers" to customers
            )

            preferences["user"] = oldUser

            // When
            preferencesMigrator.migrate(preferences, fromSchemaVersion = 2, toSchemaVersion = 3)

            // Then
            preferences.get<Any>("user") shouldBe newUser
        }

        "3 to 4" {

            // Given
            val randomOtherProperty = "Blabla"
            val access4URefreshToken = "abcd-efgh-ijkl-mnop"

            val before = """
                {
                    "preferencesSchemaVersion": 3,
                    "user": {
                        "refreshToken": "$access4URefreshToken",
                        "randomOtherProperty": "$randomOtherProperty"
                    }
                }
            """

            val after = """
                {
                    "preferencesSchemaVersion": 4,
                    "user": {
                        "randomOtherProperty": "$randomOtherProperty"
                    },
                    "access4URefreshToken": "$access4URefreshToken"
                }
            """

            val preferences = preferences(contentsJsonString = before)

            // When
            val preferencesMigrator = SharedPreferencesSchemaMigrator()
            preferencesMigrator.migrate(preferences, fromSchemaVersion = 3, toSchemaVersion = 4)

            // Then
            preferences.toJsonObject() shouldBe after.toJsonObject()
        }

    }

    private fun preferences(contentsJsonString: String? = null): SharedPreferences = FakePreferences().apply {
        if (contentsJsonString != null) {
            this += contentsJsonString.toJsonObject(throwOnError = true)!!
        }
    }

}