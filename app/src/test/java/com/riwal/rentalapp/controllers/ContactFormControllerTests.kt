package com.riwal.rentalapp.controllers

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.contactform.ContactFormController
import com.riwal.rentalapp.contactform.ContactFormView
import com.riwal.rentalapp.model.Contact
import com.riwal.rentalapp.model.Order
import com.riwal.rentalapp.model.OrderManager
import com.riwal.rentalapp.relaxedMockk
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.tables.forAll
import io.kotlintest.tables.headers
import io.kotlintest.tables.row
import io.kotlintest.tables.table
import io.mockk.Runs
import io.mockk.every
import io.mockk.just

class ContactFormControllerTests : StringSpec() {


    init {

        "Validate email" {

            // Given
            val table = table(
                    headers("Email", "IsValid"),
                    row("", true),
                    row("email@example.com", true),
                    row("firstname.lastname@example.com", true),
                    row("email@subdomain.example.com", true),
                    row("firstname+lastname@example.com", true),
                    row("email@123.123.123.123", true),
                    row("1234567890@example.com", true),
                    row("email@example-one.com", true),
                    row("_______@example.com", true),
                    row("email@example.name", true),
                    row("email@example.museum", true),
                    row("email@example.co.jp", true),
                    row("firstname-lastname@example.com", true),
                    row(".email@example.com", true),
                    row("email@111.222.333.44444", true),
                    row("Abc..123@example.com", true),
                    row("email..email@example.com", true),
                    row("email.@example.com", true),
                    row("plainaddress", false),
                    row("#@%^%#$@#$@#.com", false),
                    row("@example.com", false),
                    row("Joe Smith <email@example.com>", false),
                    row("email.example.com", false),
                    row("email@example@example.com", false),
                    row("あいうえお@example.com", false),
                    row("email@example.com (Joe Smith)", false),
                    row("email@example", false),
                    row("email@-example.com", false),
                    row("email@example..com", false),
                    row("email@[123.123.123.123]", false),
                    row("“email”@example.com", false))

            forAll(table) { email, isValid ->

                val contact: Contact = relaxedMockk { every { this@relaxedMockk.email } returns email }
                val order: Order = relaxedMockk { every { this@relaxedMockk.contact } returns contact }
                val orderManager: OrderManager = relaxedMockk {
                    every { currentOrder } returns order
                    every { save() } just Runs
                }
                val view: ContactFormView = relaxedMockk()
                val controller = controller(orderManager = orderManager)

                // when
                controller.onEmailInputChanged(email)

                // then
                controller.isValidEmail(view = view) shouldBe isValid
            }
        }

    }

    fun controller(
            view: ContactFormView = relaxedMockk(),
            orderManager: OrderManager = relaxedMockk(),
            analytics: RentalAnalytics = relaxedMockk()
    ) = ContactFormController(view, orderManager, analytics)

}