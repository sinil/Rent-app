package com.riwal.rentalapp.model

import com.riwal.rentalapp.model.api.BackendClient
import com.riwal.rentalapp.model.api.TrainingFireBaseRequestBody
import com.riwal.rentalapp.model.api.TrainingRequestBody

class TrainingManager(
        private val sessionManager: SessionManager,
        private val backend: BackendClient
) {


    /*---------------------------------------- Properties ----------------------------------------*/


//    private var observers: List<Observer> = emptyList()

    val user
        get() = sessionManager.user

    val contact
         get() = user?.asContact()


    /*----------------------------------------- Lifecycle ----------------------------------------*/



    /*----------------------------------------- Methods ------------------------------------------*/


    suspend fun getTrainingRequest(companyId: Country) = backend.getTrainings(companyId)

    suspend fun requestTrainingRequestFirebase(trainingFireBaseRequestBody: TrainingFireBaseRequestBody) =  backend.sendTrainingRequest(trainingFireBaseRequestBody)

//    fun addObserver(observer: Observer, transferId: String) {
//        observers = observers + observer
//        observer.onNotificationStatusChanged(orderManager = this, transferId = transferId)
//    }
//
//    fun removeObserver(observer: Observer) {
//        observers = observers - observer
//    }


    /*------------------------------------------- Events -----------------------------------------*/

    /*-------------------------------------- Private methods -------------------------------------*/

    /*-------------------------------------- interfaces -------------------------------------*/


}