package com.riwal.rentalapp.models

import com.riwal.rentalapp.common.extensions.datetime.*
import com.riwal.rentalapp.model.DayOfWeek.SATURDAY
import com.riwal.rentalapp.model.DayOfWeek.SUNDAY
import com.riwal.rentalapp.model.ValidOnRentPeriodCalculator
import com.riwal.rentalapp.resetNow
import com.riwal.rentalapp.setNow
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.mockk

class ValidOnRentPeriodCalculatorTests : StringSpec() {

    init {

        "Given it is before the next day delivery cutoff time, then I should be able to start renting tomorrow" {

            // Given
            val now = "2018-01-01T11:59".toLocalDateTime()!!
            val nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!
            val weekend = listOf(SATURDAY, SUNDAY)
            val rentalPeriodCalculator = ValidOnRentPeriodCalculator(nextDayDeliveryCutoffTime = nextDayDeliveryCutoffTime, weekend = weekend)

            // Then
            setNow(now.toDateTime(), freezeTime = true)
            rentalPeriodCalculator.earliestValidOnRentDateTime() shouldBe tomorrow.toLocalDateTimeAtStartOfDay()
            resetNow()
        }

        "Given it is after the next day delivery cutoff time, then I should be able to start renting the day after tomorrow" {

            // Given
            val now = "2018-01-01T12:01".toLocalDateTime()!!
            val nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!
            val weekend = listOf(SATURDAY, SUNDAY)
            val rentalPeriodCalculator = ValidOnRentPeriodCalculator(nextDayDeliveryCutoffTime = nextDayDeliveryCutoffTime, weekend = weekend)

            // Then
            setNow(now.toDateTime(), freezeTime = true)
            rentalPeriodCalculator.earliestValidOnRentDateTime() shouldBe tomorrow.plusDays(1).toLocalDateTimeAtStartOfDay()
            resetNow()
        }

        "Given it is weekend, then I should be able to start renting two days after the weekend" {

            // Given
            val now = "2019-01-05T12:00".toLocalDateTime()!!
            val nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!
            val weekend = listOf(SATURDAY, SUNDAY)
            val rentalPeriodCalculator = ValidOnRentPeriodCalculator(nextDayDeliveryCutoffTime = nextDayDeliveryCutoffTime, weekend = weekend)

            // Then
            setNow(now.toDateTime(), freezeTime = true)
            rentalPeriodCalculator.earliestValidOnRentDateTime() shouldBe "2019-01-08".toLocalDate()!!.toLocalDateTimeAtStartOfDay()
            resetNow()
        }

        "Given it is after the next day delivery cutoff time and tomorrow it is weekend, then I should be able to start renting two days after the weekend" {

            // Given
            val now = "2019-01-04T16:00".toLocalDateTime()!!
            val nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!
            val weekend = listOf(SATURDAY, SUNDAY)
            val rentalPeriodCalculator = ValidOnRentPeriodCalculator(nextDayDeliveryCutoffTime = nextDayDeliveryCutoffTime, weekend = weekend)

            // Then
            setNow(now.toDateTime(), freezeTime = true)
            rentalPeriodCalculator.earliestValidOnRentDateTime() shouldBe "2019-01-08".toLocalDate()!!.toLocalDateTimeAtStartOfDay()
            resetNow()
        }

        "Given the off rent datetime is set, then I should be able to start renting one hour before the off rent datetime " {

            // Given
            val offRentDate = "2018-01-01".toLocalDate()!!
            val offRentTime = "12:00".toLocalTime()!!
            val rentalPeriodCalculator = ValidOnRentPeriodCalculator(
                    nextDayDeliveryCutoffTime = mockk(),
                    weekend = mockk()
            )

            // Then
            rentalPeriodCalculator.latestValidOnRentDateTime(offRentDate, offRentTime) shouldBe "2018-01-01T11:00".toLocalDateTime()
        }

        "Given only the off rent date is set, then I should be able to start renting on the off rent date till 23:00" {

            // Given
            val offRentDate = "2018-01-01".toLocalDate()!!
            val rentalPeriodCalculator = ValidOnRentPeriodCalculator(
                    nextDayDeliveryCutoffTime = mockk(),
                    weekend = mockk()
            )

            // Then
            rentalPeriodCalculator.latestValidOnRentDateTime(offRentDate, offRentTime = null) shouldBe offRentDate.withTime("23:00".toLocalTime()!!)
        }

        "Given no off rent date is set, then I should be able to start renting whenever I want" {

            // Given
            val rentalPeriodCalculator = ValidOnRentPeriodCalculator(nextDayDeliveryCutoffTime = mockk(), weekend = mockk())

            // Then
            rentalPeriodCalculator.latestValidOnRentDateTime(offRentDate = null, offRentTime = null) shouldBe DISTANT_FUTURE.toLocalDateTime()
        }

    }

}