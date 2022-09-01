package com.riwal.rentalapp.welcome

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Country

interface WelcomeView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateToPickCountryPage(preparationHandler: ControllerPreparationHandler)
    fun navigateToMainPage()

    interface DataSource {
        fun activeCountry(view: WelcomeView): Country?
        fun canContinue(view: WelcomeView): Boolean
    }

    interface Delegate {
        fun onPickCountrySelected()
        fun onContinueSelected()
    }

}