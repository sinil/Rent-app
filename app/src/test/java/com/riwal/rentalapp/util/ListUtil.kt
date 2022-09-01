package com.riwal.rentalapp.util

import com.riwal.rentalapp.common.extensions.core.elementAfter
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class ListUtilTests : StringSpec() {

    init {

        "elementAfter" {

            val list = listOf(1, 2, 3)

            list.elementAfter(1) shouldBe 2
            list.elementAfter(2) shouldBe 3
            list.elementAfter(3) shouldBe null
            list.elementAfter(4) shouldBe null

            list.elementAfter(1, wrap = true) shouldBe 2
            list.elementAfter(2, wrap = true) shouldBe 3
            list.elementAfter(3, wrap = true) shouldBe 1
            list.elementAfter(4, wrap = true) shouldBe null
        }

    }

}