package com.riwal.rentalapp.training.trainingdetail

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.ContactInfo
import com.riwal.rentalapp.model.api.TrainingCourse

interface TrainingDetailView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateToRequestQuotePage(preparationHandler: ControllerPreparationHandler)
    fun navigateBack()

    interface DataSource {
        fun trainingCourse(view: TrainingDetailView): TrainingCourse
        fun isChatEnabled(view: TrainingDetailView): Boolean
        fun isPhoneCallEnable(view: TrainingDetailView): Boolean
        fun rentalDeskContactInfo(view: TrainingDetailView): List<ContactInfo>
        fun numberOfUnreadMessages(view: TrainingDetailView): Int
    }

    interface Delegate {
        fun onRequestQuoteSelected()
    }

}