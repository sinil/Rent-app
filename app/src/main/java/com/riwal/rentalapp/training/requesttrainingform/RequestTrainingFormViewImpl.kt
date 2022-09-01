package com.riwal.rentalapp.training.requesttrainingform

import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.codertainment.materialintro.sequence.SkipLocation
import com.codertainment.materialintro.shape.Focus
import com.codertainment.materialintro.shape.ShapeType
import com.codertainment.materialintro.utils.materialIntro
import com.codertainment.materialintro.utils.materialIntroSequence
import com.jaredrummler.materialspinner.MaterialSpinner
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.activity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.startActivity
import com.riwal.rentalapp.common.extensions.core.isEmail
import com.riwal.rentalapp.common.extensions.datetime.today
import com.riwal.rentalapp.common.extensions.datetime.tomorrow
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.onDateSet
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.range
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.Dialogs
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.api.*
import com.riwal.rentalapp.training.trainingtypes.TrainingTypesViewImpl
import com.riwal.rentalapp.webview.WebViewImpl
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.view_request_training_quote.*
import kotlinx.android.synthetic.main.view_request_training_quote.tutorialLayer
import kotlinx.android.synthetic.main.view_training_detail.*
import org.joda.time.LocalDate


class RequestTrainingFormViewImpl : RentalAppNotificationActivity(), RequestTrainingFormView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate, MaterialSpinner.OnItemSelectedListener<String> {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: RequestTrainingFormView.DataSource
    override lateinit var delegate: RequestTrainingFormView.Delegate

    var trainingRequestFirebaseBody = TrainingFireBaseRequestBody()

    private var depotId = String()
    private var depotEmail = String()
    private var trainingLocation = String()
    private var participant = String()

    private val contact
        get() = dataSource.contact(view = this)

    private val isValidEmail
        get() = dataSource.isValidEmail(view = this)

    private val activeCountry
        get() = dataSource.activeCountry(view = this)

    private val trainingCourse: TrainingCourse
        get() = dataSource.trainingCourse(view = this)

    private val depots: List<TrainingDepot>
        get() = dataSource.depots(view = this)

    private val participantsNumber: List<Int>
        get() = dataSource.participantsNumber(view = this)

    private val maxNumberOfParticipant: Int
        get() = dataSource.maxNumberOfParticipants(view = this)

    private val minimumParticipant: Int
        get() = dataSource.minimumNumberOfParticipants(view = this)

    private lateinit var depotsNamesAdapter: ArrayAdapter<String>


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_request_training_quote)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = trainingCourse.Name

        startDateEditTextView.onTextChangedListener = { text -> delegate.onStartDateInputChanged(text) }
        companyNameEditTextView.onTextChangedListener = { text -> delegate.onCompanyNameInputChanged(text) }
        nameEditTextView.onTextChangedListener = { text -> delegate.onNameInputChanged(text) }
        phoneEditTextView.onTextChangedListener = { text -> delegate.onPhoneInputChanged(text) }
        commentsEditTextView.onTextChangedListener = { text -> delegate.onCommentsInputChanged(text) }
        emailEditTextView.onTextChangedListener = { text ->
            if (emailEditTextView.text.toString().isEmail) {
                emailEditTextView.error = null
            } else {
                emailEditTextView.error = getString(R.string.error_invalid_mail_address)
            }

            delegate.onEmailInputChanged(text)
        }

        participantEditTextView.hint = getString(R.string.participants) + " ($minimumParticipant - $maxNumberOfParticipant)"

        participantEditTextView.onTextChangedListener = { text ->

            if (text.isNotEmpty()) {
                if (participantsNumber.contains(text.toInt())) {
                    participant = text
                    delegate.onParticipantsInputChanged(text)
                    participantEditTextView.error = null
                } else {
                    participantEditTextView.error = getString(R.string.participant_validation, minimumParticipant.toString(), maxNumberOfParticipant.toString())
                }
            } else {
                participantEditTextView.error = null
                participant = text
                delegate.onParticipantsInputChanged(text)
            }
        }


        delegate.updateCourseId(trainingCourse.Id)
        companyNameEditTextView.text = contact?.company
        nameEditTextView.text = contact?.name
        phoneEditTextView.text = contact?.phoneNumber
        if (!(contact?.email.isNullOrBlank())) emailEditTextView.text = contact?.email


        //Depots Spinner
        locationSpinner.setOnItemSelectedListener(this)
        val depotList = listOf<String>(getString(R.string.location)) + depots.map { depot -> depot.Name }

        depotsNamesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, depotList)
        depotsNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.setAdapter(depotsNamesAdapter)


        startDateLayout.setOnClickListener {
            showDatePicker { date ->
                //                delegate.onOnRentDatePicked(date)
                startDateEditTextView.text = date.toString()

            }
        }
        sendRequestButton.setOnClickListener {

            val training = Training(courseId = trainingCourse.Id, depotId = depotId, trainingName = trainingCourse.Name,
                    trainingCategory = trainingCourse.Category.Name, location = trainingLocation,
                    startDate = startDateEditTextView.text, participantes = participantEditTextView.text.toString(),
                    depotEmail = depotEmail)

            val trainingCustomer = TrainingCustomer(name = nameEditTextView.text.toString(),
                    phoneNumber = phoneEditTextView.text.toString(), email = emailEditTextView.text.toString(),
                    company = companyNameEditTextView.text.toString())

            trainingRequestFirebaseBody = trainingRequestFirebaseBody.copy(comment = commentsEditTextView.text.toString(),
                    training = training, customer = trainingCustomer, countryCode = activeCountry)

            activityIndicator.visibility = View.VISIBLE

            delegate.onSendRequestButtonSelected(trainingRequestFirebaseBody)

        }

        val privacyPolicy = SpannableString(getString(R.string.privacy_policy_message))
        privacyPolicy.setSpan(UnderlineSpan(), 0, privacyPolicy.length, 0)
        privacyPolicyText.text = privacyPolicy
        privacyPolicyLink.setOnClickListener {
            delegate.onPrivacyPolicyClicked()
        }
        privacyPolicyCheckBox.setOnCheckedChangeListener { _, isChecked ->
            delegate.onPrivacyPolicyChecked(isChecked)
        }

        Handler().postDelayed({
            showMaterialIntro()

        }, 800)
    }

    private fun showMaterialIntro() {
        tutorialLayer.visibility = View.VISIBLE

        materialIntroSequence(1000, showSkip = true, persistSkip = true) {
            addConfig {
                focusType = Focus.NONE
                infoText = getString(R.string.tutorial_message_training_request_form)
                okText = getString(R.string.next_button_title)
                skipText = getString(R.string.skip_tutorial_label)
                skipLocation = SkipLocation.TOP_RIGHT
                shapeType = ShapeType.CIRCLE
                targetView = contentLayout
                viewId = "contentLayout"
            }

            addConfig {
                infoText = getString(R.string.tutorial_message_training_request_send)
                okText = getString(R.string.done)
                skipText = getString(R.string.skip_tutorial_label)
                skipLocation = SkipLocation.TOP_RIGHT
                shapeType = ShapeType.RECTANGLE
                targetView = sendRequestButton
                viewId = "sendRequestButton"
                layerView = this@RequestTrainingFormViewImpl.tutorialLayer
                dismissOnClick = true
            }
        }
    }

    override fun updateUI(animated: Boolean) {
        super.updateUI(animated)
        sendRequestButton.isEnabled = dataSource.canRequestTraining(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*---------------------------------------- Actions ------------------------------------------*/

    override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String?) {

        if (position != 0) view?.setTextColor(Color.BLACK) else view?.setTextColor(ContextCompat.getColor(this, R.color.material_grey_500))
        when (view?.id) {
            R.id.locationSpinner -> {
                if (item != null) {
                    if (position == 0) {
                        delegate.onLocationInputChanged("")
                    } else {
                        depotId = depots.first { it.Name == item }.Id
                        depotEmail = depots.firstOrNull { it.Name == item }?.EmailId ?: ""
                        trainingLocation = item
                        delegate.onLocationInputChanged(depots.first { it.Name == item }.Id)
                    }
                }

            }

        }
    }


    /*---------------------------- TrainingCategoriesView methods --------------------------------*/


    override fun navigateToTrainingTypes() = startActivity(TrainingTypesViewImpl::class, Intent.FLAG_ACTIVITY_CLEAR_TOP)

    override fun trainingRequestBody() = trainingRequestFirebaseBody


    override fun showSuccess(completionHandler: () -> Unit) = onUiThread {

        activityIndicator.visibility = View.GONE

        AlertDialog.Builder(this)
                .setMessage(getString(R.string.training_request_succeed))
                .setPositiveButton(getString(R.string.check_other_training)) { _, _ ->
                    completionHandler()
                }
                .setCancelable(false)
                .show()

    }

    override fun navigateToWebView(preparationHandler: ControllerPreparationHandler) {
        startActivity(WebViewImpl::class, controllerPreparationHandler = preparationHandler)
    }

    /*------------------------------------- Private methods --------------------------------------*/


    private fun showDatePicker(callback: (date: LocalDate) -> Unit) {
        Dialogs.datePicker()
                .onDateSet(callback)
                .range(tomorrow)
                .show(supportFragmentManager, "DatePickerDialog")
    }


}

