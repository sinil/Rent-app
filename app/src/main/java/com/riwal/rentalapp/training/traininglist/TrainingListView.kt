package com.riwal.rentalapp.training.traininglist

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.ContactInfo
import com.riwal.rentalapp.model.api.TrainingCategory
import com.riwal.rentalapp.model.api.TrainingCourse

interface TrainingListView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateToTrainingDetailPage(preparationHandler: ControllerPreparationHandler)
    fun navigateBack()

    interface DataSource {
        fun trainingType(view: TrainingListView): TrainingCategory
        fun isChatEnabled(view: TrainingListView): Boolean
        fun isPhoneCallEnable(view: TrainingListView): Boolean
        fun rentalDeskContactInfo(view: TrainingListView): List<ContactInfo>
        fun numberOfUnreadMessages(view: TrainingListView): Int


    }

    interface Delegate {
        fun onTrainingDetailSelected(trainingCourse:TrainingCourse)
    }

}