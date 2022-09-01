package com.riwal.rentalapp.feedback

import android.view.MenuItem
import com.hsalf.smilerating.BaseRating.*
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.core.isEmail
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_feedback.*

class FeedbackViewImpl : RentalAppNotificationActivity(), FeedbackView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var delegate: FeedbackView.Delegate


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_feedback)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = getString(R.string.page_feedback)

        ratingBar.setNameForSmile(TERRIBLE, getString(R.string.rating_terrible))
        ratingBar.setNameForSmile(BAD, getString(R.string.rating_bad))
        ratingBar.setNameForSmile(OKAY, getString(R.string.rating_okay))
        ratingBar.setNameForSmile(GOOD, getString(R.string.rating_good))
        ratingBar.setNameForSmile(GREAT, getString(R.string.rating_great))
        ratingBar.setOnRatingSelectedListener { _, _ -> updateUI() }

        sendFeedbackButton.setOnClickListener { delegate.onSendFeedbackSelected(selectedSmileAsRating, feedbackEditText.text, emailEditText.text) }
        emailEditText.onTextChangedListener = {
            updateUI()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> navigateBack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        navigateBack()
    }


    /*--------------------------------------- FeedbackView ---------------------------------------*/


    override fun navigateBack() {
        finish()
    }


    override fun clearFields() {
        ratingBar.selectedSmile = NONE
        feedbackEditText.getText()?.clear()
        emailEditText.getText()?.clear()
        updateUI()
    }


    /*-------------------------------------- Private methods -------------------------------------*/


    override fun updateUI(animated: Boolean) {

        val email = emailEditText.text!!
        val hasRating = (ratingBar.selectedSmile != NONE)
        val hasValidEmail = email.isEmpty() || email.isEmail

        emailTextInputLayout.isErrorEnabled = !hasValidEmail
        emailTextInputLayout.error = if (hasValidEmail) null else getString(R.string.error_invalid_mail_address)
        sendFeedbackButton.isEnabled = (hasRating && hasValidEmail)
    }


    /*---------------------------------------- Extensions ----------------------------------------*/


    private val selectedSmileAsRating
        get() = when (ratingBar.selectedSmile) {
            TERRIBLE -> "1/5"
            BAD -> "2/5"
            OKAY -> "3/5"
            GOOD -> "4/5"
            GREAT -> "5/5"
            else -> ""
        }


}