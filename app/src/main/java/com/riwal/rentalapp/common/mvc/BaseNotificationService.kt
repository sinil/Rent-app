package com.riwal.rentalapp.common.mvc

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.RegisterControllerPreparationHandlerRequest
import com.riwal.rentalapp.ServiceCreatedEvent
import com.riwal.rentalapp.common.extensions.android.startActivity
import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.common.ui.ObservableLifecycleService
import com.riwal.rentalapp.common.ui.ServiceLifecycleObserver
import kotlin.reflect.KClass

@SuppressLint("Registered")
open class BaseNotificationService : FirebaseMessagingService(), ObservableLifecycleService {


    /*---------------------------------------- Properties ----------------------------------------*/


    override var lifecycleObservers: List<ServiceLifecycleObserver> = emptyList()


    /*---------------------------------------- Lifecycle -----------------------------------------*/

    override fun onCreate() {
        super.onCreate()

        postEvent(ServiceCreatedEvent(viewToken = this::class, service = this))

        notifyCreate()
    }

    override fun onDestroy() {
        super.onDestroy()

        notifyDestroy()
    }


    /*------------------------------------------ Methods -----------------------------------------*/


    fun <T : BaseActivity> startActivity(activityClass: KClass<T>, controllerPreparationHandler: ControllerPreparationHandler) {
        postEvent(RegisterControllerPreparationHandlerRequest(viewToken = activityClass, preparationHandler = controllerPreparationHandler))
        startActivity(activityClass)
    }
}