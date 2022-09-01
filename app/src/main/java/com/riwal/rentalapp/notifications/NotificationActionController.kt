package com.riwal.rentalapp.notifications

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.main.MainController
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.model.SessionManager
import com.riwal.rentalapp.rentaldetail.RentalDetailController

class NotificationActionController(
        val service: NotificationActionService,
        val sessionManager: SessionManager,
        val rentalAnalytics: RentalAnalytics
) : NotificationActionService.DataSource, NotificationActionService.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    private val isLoggedIn
        get() = sessionManager.isLoggedIn


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        service.dataSource = this
        service.delegate = this
    }


    /*---------------------------------------- DataSource ----------------------------------------*/


    /*----------------------------------------- Delegate -----------------------------------------*/


    override fun onOffRentNotificationSelected(rental: Rental) {

        rentalAnalytics.offRentNotificationSelected(rental)

        if (!isLoggedIn) {
            return
        }

        if (service.isMainPageCreated) {
            service.navigateToRentalDetailsPage { controller ->
                controller as RentalDetailController
                controller.rental = rental
                controller.isViewPresentedModally = true
            }
        } else {
            service.navigateToMainPage { controller ->
                controller as MainController
                controller.rentalFromNotification = rental
            }
        }

    }


}