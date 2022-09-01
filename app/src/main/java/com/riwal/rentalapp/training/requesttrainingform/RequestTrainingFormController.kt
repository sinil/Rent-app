package com.riwal.rentalapp.training.requesttrainingform

import android.content.res.Resources
import com.riwal.rentalapp.R
import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.core.isEmail
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.CountryManager
import com.riwal.rentalapp.model.TrainingManager
import com.riwal.rentalapp.model.api.TrainingCourse
import com.riwal.rentalapp.model.api.TrainingDepot
import com.riwal.rentalapp.model.api.TrainingFireBaseRequestBody
import com.riwal.rentalapp.model.api.TrainingRequestBody
import com.riwal.rentalapp.webview.WebViewController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RequestTrainingFormController(val view: RequestTrainingFormView,
                                    val trainingManager: TrainingManager,
                                    val countryManager: CountryManager,
                                    val analytics: RentalAnalytics,
                                    val resources: Resources,
                                    val activeCountry: Country) : ViewLifecycleObserver, RequestTrainingFormView.DataSource, RequestTrainingFormView.Delegate, CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    private val contact
        get() = trainingManager.contact

    lateinit var trainingCourse: TrainingCourse

    var trainingRequestBody = TrainingRequestBody()

    private val isValidEmail: Boolean
        get() = if (contact?.email != null) {
            contact?.email!!.isEmpty() || contact?.email!!.isEmail
        } else false

    private val canRequestTraining
        get() = trainingRequestBody.canSendRequest && isPrivacyPolicyChecked

    private val depots: List<TrainingDepot>
        get() = trainingCourse.Depots

    private val maxNumberOfParticipants: Int
        get() = if (trainingCourse.Participants.Maximum > 0) trainingCourse.Participants.Maximum else 100

    private val minimumNumberParticipant: Int
        get() = trainingCourse.Participants.Minimum

    private val participantsNumber: List<Int>
        get() = (trainingCourse.Participants.Minimum..maxNumberOfParticipants).toList()

    private var isPrivacyPolicyChecked = false
        set(value) {
            field = value
            view.notifyDataChanged()
        }


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.userLookingAtOrderContactForm()
    }

    /*--------------------------- TrainingCategoriesView DataSource ------------------------------*/


    override fun trainingCourse(view: RequestTrainingFormView) = trainingCourse
    override fun depots(view: RequestTrainingFormView): List<TrainingDepot> = depots
    override fun maxNumberOfParticipants(view: RequestTrainingFormView): Int = maxNumberOfParticipants
    override fun minimumNumberOfParticipants(view: RequestTrainingFormView): Int = minimumNumberParticipant
    override fun participantsNumber(view: RequestTrainingFormView): List<Int> = participantsNumber
    override fun contact(view: RequestTrainingFormView) = contact
    override fun isValidEmail(view: RequestTrainingFormView) = isValidEmail
    override fun canRequestTraining(view: RequestTrainingFormView) = canRequestTraining
    override fun activeCountry(view: RequestTrainingFormView) = activeCountry


    /*-------------------------- TrainingCategoriesView Delegate ---------------------------------*/


    override fun onLocationInputChanged(text: String) {
        trainingRequestBody = trainingRequestBody.copy(depotId = text)
        view.notifyDataChanged()

    }

    override fun updateCourseId(id: String) {
        trainingRequestBody = trainingRequestBody.copy(courseId = id)
    }

    override fun onStartDateInputChanged(text: String) {
        trainingRequestBody = trainingRequestBody.copy(startDate = text)
        view.notifyDataChanged()

    }

    override fun onParticipantsInputChanged(text: String) {
        trainingRequestBody = trainingRequestBody.copy(participantsCount = text)
        view.notifyDataChanged()

    }

    override fun onCompanyNameInputChanged(text: String) {
        trainingRequestBody = trainingRequestBody.copy(custCompanyName = text)
        view.notifyDataChanged()

    }

    override fun onNameInputChanged(text: String) {
        trainingRequestBody = trainingRequestBody.copy(custContactName = text)
        view.notifyDataChanged()

    }

    override fun onPhoneInputChanged(text: String) {
        trainingRequestBody = trainingRequestBody.copy(custContactPhone = text)
        view.notifyDataChanged()

    }

    override fun onEmailInputChanged(text: String) {
        trainingRequestBody = trainingRequestBody.copy(custContactEmail = text)
        view.notifyDataChanged()

    }

    override fun onCommentsInputChanged(text: String) {
        trainingRequestBody = trainingRequestBody.copy(comments = text)
        view.notifyDataChanged()

    }

    override fun onPrivacyPolicyClicked() {
        view.navigateToWebView { destination ->
            val controller = destination as WebViewController
            controller.pageTitle = resources.getString(R.string.setting_privacy_policy)
            controller.pageUrl = resources.getString(R.string.privacy_policy_url)
        }
    }

    override fun onPrivacyPolicyChecked(isChecked: Boolean) {
        isPrivacyPolicyChecked = isChecked
        trainingRequestBody = trainingRequestBody.copy(countryCode = activeCountry)
    }

    override fun onSendRequestButtonSelected(trainingRequestFirebaseBody: TrainingFireBaseRequestBody) {

        analytics.sendRequestSelected()

        launch {
            trainingManager.requestTrainingRequestFirebase(trainingRequestFirebaseBody)
            view.showSuccess {
                analytics.requestTrainingConfirmed()
                view.navigateToTrainingTypes()
            }
        }
    }


}