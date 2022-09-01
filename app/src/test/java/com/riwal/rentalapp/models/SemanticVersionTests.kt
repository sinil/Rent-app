package com.riwal.rentalapp.models

import com.riwal.rentalapp.model.toSemanticVersion
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec

class SemanticVersionTests : StringSpec() {

    init {

        "Semantic version comparison" {

            ("0.1.0".toSemanticVersion() < "0.2.0".toSemanticVersion()) shouldBe true
            ("0.0.100".toSemanticVersion() < "0.1.0".toSemanticVersion()) shouldBe true

            ("1.0.0".toSemanticVersion() > "0.9999.9999".toSemanticVersion()) shouldBe true
            ("0.10.0".toSemanticVersion() > "0.2.0".toSemanticVersion()) shouldBe true
            ("10.0.0".toSemanticVersion() > "9.9.10".toSemanticVersion()) shouldBe true

            "1.2.3".toSemanticVersion() shouldBe "1.2.3".toSemanticVersion()
            "1.2.3".toSemanticVersion() shouldNotBe "1.2.30".toSemanticVersion()
        }

    }

}