package com.riwal.rentalapp.feedback

import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.RentalManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FeedbackController(
        val view: FeedbackView,
        val rentalManager: RentalManager,
        val country: Country
) : ViewLifecycleObserver, FeedbackView.Delegate, CoroutineScope by MainScope() {


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.delegate = this
        observe(view)
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        cancel()
    }


    /*------------------------------------- Feedback.Delegate ------------------------------------*/


    override fun onSendFeedbackSelected(rating: String, message: String?, email: String?) {
        launch {
            rentalManager.sendFeedback(rating, message, email, country)
            view.clearFields()
        }
    }
}