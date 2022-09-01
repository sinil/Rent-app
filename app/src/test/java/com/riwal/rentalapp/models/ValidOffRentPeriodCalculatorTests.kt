package com.riwal.rentalapp.models

import com.riwal.rentalapp.common.extensions.datetime.*
import com.riwal.rentalapp.model.ValidOffRentPeriodCalculator
import com.riwal.rentalapp.resetNow
import com.riwal.rentalapp.setNow
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.mockk

class ValidOffRentPeriodCalculatorTests : StringSpec() {

    init {

        "Given the current time is before the same day off rent cutoff time, then I should be able to stop renting now" {

            // Given
            val now = "2018-01-01T11:59".toLocalDateTime()!!
            val sameDayOffRentCutoffTime = "12:00".toLocalTime()!!
            val offRentPeriodCalculator = ValidOffRentPeriodCalculator(sameDayOffRentCutoffTime)

            setNow(now.toDateTime(), freezeTime = true)

            // Then
            val earliestOffRentDateTime = offRentPeriodCalculator.earliestValidOffRentDateTime(onRentDate = null, onRentTime = null)
            earliestOffRentDateTime shouldBe now

            resetNow()
        }

        "Given the current time is exactly the same-day-off-rent-cutoff-time, then I should be able to stop renting now" {

            // Given
            val now = "2018-01-01T12:00".toLocalDateTime()!!
            val sameDayOffRentCutoffTime = "12:00".toLocalTime()!!
            val offRentPeriodCalculator = ValidOffRentPeriodCalculator(sameDayOffRentCutoffTime)

            setNow(now.toDateTime(), freezeTime = true)

            // Then
            val earliestOffRentDateTime = offRentPeriodCalculator.earliestValidOffRentDateTime(onRentDate = null, onRentTime = null)
            earliestOffRentDateTime shouldBe now

            resetNow()
        }

        "Given the current time is after the same-day-off-rent-cutoff-time, then I should be able to stop renting from tomorrow" {

            // Given
            val now = "2018-01-01T12:01".toLocalDateTime()!!
            val sameDayOffRentCutoffTime = "12:00".toLocalTime()!!
            val offRentPeriodCalculator = ValidOffRentPeriodCalculator(sameDayOffRentCutoffTime)

            setNow(now.toDateTime(), freezeTime = true)

            // Then
            val earliestOffRentDateTime = offRentPeriodCalculator.earliestValidOffRentDateTime(onRentDate = null, onRentTime = null)
            earliestOffRentDateTime shouldBe tomorrow.toLocalDateTimeAtStartOfDay()

            resetNow()
        }

        "Given the on rent datetime is set, then I should be able to stop renting from one hour after the on rent datetime" {

            // Given
            val onRentDate = "2018-01-01".toLocalDate()!!
            val onRentTime = "12:00".toLocalTime()!!
            val rentalPeriodCalculator = ValidOffRentPeriodCalculator(sameDayOffRentCutoffTime = mockk())

            // Then
            val earliestOffRentDateTime = rentalPeriodCalculator.earliestValidOffRentDateTime(onRentDate, onRentTime)
            earliestOffRentDateTime shouldBe "2018-01-01T13:00".toLocalDateTime()
        }

    }
}