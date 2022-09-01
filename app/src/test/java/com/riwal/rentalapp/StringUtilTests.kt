package com.riwal.rentalapp

import com.riwal.rentalapp.common.extensions.core.StringCompareOptions.NUMERIC
import com.riwal.rentalapp.common.extensions.core.compareTo
import com.riwal.rentalapp.common.extensions.core.splitWhere
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class StringUtilTests : StringSpec() {

    init {

        "Given 2 strings with numeric components, when I compare using the numeric option, then I expect the ordering to be as expected" {

            val string1 = "test1"
            val string2 = "test3"
            val string3 = "test200"
            val string4 = "test200.1"
            val string5 = "test200.2"
            val string6 = "test_200"
            val string7 = "test_0200"

            string1.compareTo(string2, options = setOf(NUMERIC)) shouldBeLessThan 0
            string2.compareTo(string3, options = setOf(NUMERIC)) shouldBeLessThan 0
            string3.compareTo(string4, options = setOf(NUMERIC)) shouldBeLessThan 0
            string4.compareTo(string5, options = setOf(NUMERIC)) shouldBeLessThan 0
            string5.compareTo(string6, options = setOf(NUMERIC)) shouldBeLessThan 0

            string6.compareTo(string5, options = setOf(NUMERIC)) shouldBeGreaterThan 0
            string5.compareTo(string4, options = setOf(NUMERIC)) shouldBeGreaterThan 0
            string4.compareTo(string3, options = setOf(NUMERIC)) shouldBeGreaterThan 0
            string3.compareTo(string2, options = setOf(NUMERIC)) shouldBeGreaterThan 0
            string2.compareTo(string1, options = setOf(NUMERIC)) shouldBeGreaterThan 0

            string6.compareTo(string7, options = setOf(NUMERIC)) shouldBe 0
        }

        "Split string on condition" {

            "123test456".splitWhere { c1, c2 -> c1 == '3' || c2 == '4' } shouldBe listOf("123", "test", "456")
            "123test456".splitWhere { c1, c2 -> c1.isLetter() || c2.isLetter() } shouldBe listOf("123", "t", "e", "s", "t", "456")
            "123test456".splitWhere { c1, c2 -> c1.isDigit() != c2.isDigit() } shouldBe listOf("123", "test", "456")
            "123test456".splitWhere { c1, c2 -> "$c1$c2" == "es" } shouldBe listOf("123te", "st456")

        }

    }

}