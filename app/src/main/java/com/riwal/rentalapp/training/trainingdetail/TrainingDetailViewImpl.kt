package com.riwal.rentalapp.training.trainingdetail

import android.os.Handler
import android.view.MenuItem
import android.view.View
import com.codertainment.materialintro.sequence.SkipLocation
import com.codertainment.materialintro.shape.Focus
import com.codertainment.materialintro.shape.ShapeType
import com.codertainment.materialintro.utils.materialIntro
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.activity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.training.requesttrainingform.RequestTrainingFormViewImpl
import kotlinx.android.synthetic.main.view_training.*
import kotlinx.android.synthetic.main.view_training_detail.*
import kotlinx.android.synthetic.main.view_training_detail.certificationTextView
import kotlinx.android.synthetic.main.view_training_detail.chatButton
import kotlinx.android.synthetic.main.view_training_detail.descriptionTextView
import kotlinx.android.synthetic.main.view_training_detail.durationTextView
import kotlinx.android.synthetic.main.view_training_detail.participantTextView
import kotlinx.android.synthetic.main.view_training_detail.phoneCallButton
import kotlinx.android.synthetic.main.view_training_detail.priceTextView
import kotlinx.android.synthetic.main.view_training_detail.toolbar
import kotlinx.android.synthetic.main.view_training_detail.tutorialLayer


class TrainingDetailViewImpl : RentalAppNotificationActivity(), TrainingDetailView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: TrainingDetailView.DataSource
    override lateinit var delegate: TrainingDetailView.Delegate

    private val training
        get() = dataSource.trainingCourse(view = this)

    private val isChatEnabled
        get() = dataSource.isChatEnabled(view = this)

    private val isPhoneCallEnable
        get() = dataSource.isPhoneCallEnable(view = this)

    private val rentalContactNumber
        get() = dataSource.rentalDeskContactInfo(view = this)[0].phoneNumber

    private val numberOfUnreadMessages
        get() = dataSource.numberOfUnreadMessages(view = this)

    private var isTutorialDisplayed = false


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_training_detail)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = getString(R.string.training_details_title)

        requestQuoteButton.setOnClickListener { delegate.onRequestQuoteSelected() }

        updateUI(false)


    }

    private fun showMaterialIntro() {
        tutorialLayer.visibility = View.VISIBLE
        materialIntro(true) {
            layerView = this@TrainingDetailViewImpl.tutorialLayer
            focusType = Focus.NORMAL
            infoText = getString(R.string.tutorial_message_training_detail)
            okText = getString(R.string.next_button_title)
            skipText = activity.getString(R.string.skip_tutorial_label)
            skipLocation = SkipLocation.TOP_RIGHT
            shapeType = ShapeType.RECTANGLE
            targetView = requestQuoteButton
            viewId = "requestQuoteButton"
            clickAction = {
                delegate.onRequestQuoteSelected()
            }
            isTutorialDisplayed = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> navigateBack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun updateUI(animated: Boolean) {

        chatButton.isVisible = isChatEnabled
        chatButton.numberOfUnreadMessages = numberOfUnreadMessages

        phoneCallButton.isVisible = isPhoneCallEnable
        phoneCallButton.phoneNumber = rentalContactNumber

        durationTextView.text = training.Duration.trim()
        if (training.Certification != null) {
            certificationTextView.text = training.Certification.trim()
        } else {
            certificationTextView.visibility = View.GONE
            certificationLabelView.visibility = View.GONE

        }
        participantTextView.text = training.Participants.StringValue.trim()
        priceTextView.text = training.Price.trim()
        locationTextView.text = training.CourseDepots.trim()
        courseTextView.text = training.Name.trim()

        if (training.LongDesc != null) {
            descriptionTextView.text = training.LongDesc.trim()
        } else {
            descriptionTextView.visibility = View.GONE
        }

        Handler().postDelayed({
            if (!isTutorialDisplayed)
                showMaterialIntro()

        }, 800)


    }


    /*---------------------------- TrainingCategoriesView methods --------------------------------*/

    override fun navigateToRequestQuotePage(preparationHandler: ControllerPreparationHandler) {
        startActivity(RequestTrainingFormViewImpl::class, controllerPreparationHandler = preparationHandler)
    }

    override fun navigateBack() = onBackPressed()


}