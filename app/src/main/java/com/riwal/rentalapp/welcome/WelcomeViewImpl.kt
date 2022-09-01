package com.riwal.rentalapp.welcome

import android.view.View
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.beginDelayedTransition
import com.riwal.rentalapp.common.extensions.android.contentView
import com.riwal.rentalapp.common.extensions.android.displayMetrics
import com.riwal.rentalapp.common.extensions.android.startActivity
import com.riwal.rentalapp.common.extensions.animation.onEnd
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.country.CountryViewImpl
import com.riwal.rentalapp.main.MainViewImpl
import com.riwal.rentalapp.model.Notification
import kotlinx.android.synthetic.main.view_main.*
import kotlinx.android.synthetic.main.view_welcome.*


class WelcomeViewImpl : RentalAppNotificationActivity(), WelcomeView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: WelcomeView.DataSource
    override lateinit var delegate: WelcomeView.Delegate

    private val activeCountry
        get() = dataSource.activeCountry(view = this)

    private val canContinue
        get() = dataSource.canContinue(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_welcome)

        countryButton.setOnClickListener { delegate.onPickCountrySelected() }
        continueButton.setOnClickListener { delegate.onContinueSelected() }

        updateUI()

        contentView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                contentView.removeOnLayoutChangeListener(this)
                animateImageViews()
            }
        })

    }

    override fun shouldDeferNotification(notification: Notification) = true


    /*--------------------------------------- WelcomeView ----------------------------------------*/


    override fun navigateToPickCountryPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(CountryViewImpl::class, controllerPreparationHandler = preparationHandler)
    }

    override fun navigateToMainPage() {
        startActivity(MainViewImpl::class)
        finish(transition = ModalPopActivityTransition)
    }

    /*------------------------------------- Private methods --------------------------------------*/


    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition(ParallelAutoTransition()
                    .excludeChildren(content, true))
        }

        countryTextView.text = getString(activeCountry?.localizedNameRes ?: R.string.country_none)
        countryImageView.loadImage(activeCountry?.picture ?: -1)
        continueButton.isEnabled = canContinue
    }

    private fun animateImageViews() {

        val windowWidth = displayMetrics.widthPixels.toFloat()
        val pausePosition = (windowWidth / 2) - (forkLiftImageView.width / 2)
        val offScreenPosition = 0 - (windowWidth / 3)

        forkLiftImageView.x = windowWidth
        scissorLiftImageView.x = windowWidth
        articulatedBoomLiftImageView.x = windowWidth

        forkLiftImageView.moveStopMoveAnimation(pausePosition, offScreenPosition) {

            scissorLiftImageView.moveStopMoveAnimation(pausePosition, offScreenPosition) {

                articulatedBoomLiftImageView.moveStopMoveAnimation(pausePosition, offScreenPosition) {

                    animateImageViews()

                }.start()

            }.start()

        }.start()

    }


    /*------------------------------------ Private extensions ------------------------------------*/


    private fun View.moveStopMoveAnimation(pausePosition: Float, endPosition: Float, onComplete: (() -> Unit)? = null) = animate()
            .x(pausePosition)
            .setDuration(500)
            .setStartDelay(0)
            .onEnd {
                animate()
                        .x(endPosition)
                        .setDuration(1500)
                        .setStartDelay(5000)
                        .onEnd { onComplete?.invoke() }
                        .start()
            }

}