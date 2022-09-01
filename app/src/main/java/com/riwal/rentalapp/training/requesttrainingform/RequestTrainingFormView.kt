package com.riwal.rentalapp.training.requesttrainingform

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Contact
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.api.TrainingCourse
import com.riwal.rentalapp.model.api.TrainingDepot
import com.riwal.rentalapp.model.api.TrainingFireBaseRequestBody

interface RequestTrainingFormView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateToTrainingTypes()
    fun navigateToWebView(preparationHandler: ControllerPreparationHandler)
//    fun askForComments(completionHandler: (comments: String, trainingRequest: TrainingFireBaseRequestBody) -> Unit)
    fun showSuccess(completionHandler:() -> Unit)
    fun trainingRequestBody(): TrainingFireBaseRequestBody

    interface DataSource {
        fun trainingCourse(view: RequestTrainingFormView): TrainingCourse
        fun depots(view: RequestTrainingFormView): List<TrainingDepot>
        fun maxNumberOfParticipants(view: RequestTrainingFormView):Int
        fun minimumNumberOfParticipants(view: RequestTrainingFormView):Int
        fun participantsNumber(view: RequestTrainingFormView): List<Int>
        fun contact(view: RequestTrainingFormView): Contact?
        fun isValidEmail(view: RequestTrainingFormView): Boolean
        fun canRequestTraining(view: RequestTrainingFormView): Boolean
        fun activeCountry(view: RequestTrainingFormView): Country

    }

    interface Delegate {
        fun onLocationInputChanged(text: String)
        fun onStartDateInputChanged(text: String)
        fun onParticipantsInputChanged(text: String)
        fun onCompanyNameInputChanged(text: String)
        fun onNameInputChanged(text: String)
        fun onPhoneInputChanged(text: String)
        fun onEmailInputChanged(text: String)
        fun onCommentsInputChanged(text: String)
        fun onPrivacyPolicyClicked()
        fun onPrivacyPolicyChecked(isChecked: Boolean)
        fun onSendRequestButtonSelected(trainingRequestFirebaseBody: TrainingFireBaseRequestBody)
        fun updateCourseId(id: String)
    }

}