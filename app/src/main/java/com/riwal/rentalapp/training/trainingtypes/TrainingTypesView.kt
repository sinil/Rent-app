package com.riwal.rentalapp.training.trainingtypes

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.ContactInfo
import com.riwal.rentalapp.model.api.TrainingCategory
import com.riwal.rentalapp.model.api.TrainingResponse

interface TrainingTypesView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateToTrainingCoursesPage(preparationHandler: ControllerPreparationHandler)
    fun navigateBack()

    interface DataSource {
        fun training(view: TrainingTypesView): TrainingResponse?
        fun isLoadingTraining(view: TrainingTypesView): Boolean
        fun hasFailedLoadingTraining(view: TrainingTypesView): Boolean
        fun isChatEnabled(view: TrainingTypesView): Boolean
        fun isPhoneCallEnable(view: TrainingTypesView): Boolean
        fun rentalDeskContactInfo(view: TrainingTypesView): List<ContactInfo>
        fun numberOfUnreadMessages(view: TrainingTypesView): Int

    }

    interface Delegate {
        fun onRetryLoadingTrainingSelected()
        fun onTrainingCategorySelected(view: TrainingTypesView, trainingCategory: TrainingCategory)
    }

}