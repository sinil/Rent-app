package com.riwal.rentalapp.feedback

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView

interface FeedbackView : MvcView, ObservableLifecycleView {

    var delegate: Delegate

    fun navigateBack()
    fun clearFields()

    interface Delegate {
        fun onSendFeedbackSelected(rating: String, message: String?, email: String?)
    }

}