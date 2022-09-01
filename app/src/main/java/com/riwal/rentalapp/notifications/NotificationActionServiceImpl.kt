package com.riwal.rentalapp.notifications

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.riwal.rentalapp.Application
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.extensions.json.fromJson
import com.riwal.rentalapp.common.mvc.BaseService
import com.riwal.rentalapp.main.MainViewImpl
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.notifications.NotificationActionService.DataSource
import com.riwal.rentalapp.notifications.NotificationActionService.Delegate
import com.riwal.rentalapp.notifications.NotificationType.OFF_RENT
import com.riwal.rentalapp.rentaldetail.RentalDetailViewImpl

class NotificationActionServiceImpl : BaseService(), NotificationActionService {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    override val isMainPageCreated: Boolean
        get() = (application as Application).isMainActivityCreated


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == null) {
            return super.onStartCommand(intent, flags, startId)
        }

        val action = NotificationType.valueOf(intent.action!!)
        val rentalJson = intent.getStringExtra("rental")
        val rental: Rental? = fromJson(rentalJson)

        when (action) {
            OFF_RENT -> delegate.onOffRentNotificationSelected(rental!!)
        }

        return super.onStartCommand(intent, flags, startId)
    }


    /*--------------------------------- NotificationActionService --------------------------------*/


    override fun navigateToMainPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(MainViewImpl::class, FLAG_ACTIVITY_NEW_TASK, preparationHandler)
    }

    override fun navigateToRentalDetailsPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(RentalDetailViewImpl::class, FLAG_ACTIVITY_NEW_TASK, preparationHandler)
    }

}