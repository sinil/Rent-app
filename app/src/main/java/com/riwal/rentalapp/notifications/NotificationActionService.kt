package com.riwal.rentalapp.notifications

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.model.Rental

interface NotificationActionService {

    var dataSource: DataSource
    var delegate: Delegate

    val isMainPageCreated: Boolean

    fun navigateToMainPage(preparationHandler: ControllerPreparationHandler)
    fun navigateToRentalDetailsPage(preparationHandler: ControllerPreparationHandler)

    interface DataSource

    interface Delegate {
        fun onOffRentNotificationSelected(rental: Rental)
    }

}