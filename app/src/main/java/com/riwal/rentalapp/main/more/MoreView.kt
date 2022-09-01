package com.riwal.rentalapp.main.more

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.SemanticVersion


interface MoreView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateToPickCountryPage(preparationHandler: ControllerPreparationHandler)
    fun navigateToWebView(preparationHandler: ControllerPreparationHandler)
    fun navigateToPlusPortWebView(preparationHandler: ControllerPreparationHandler)
    fun navigateToBookTrainingPage()

    interface DataSource {
        fun country(view: MoreView): Country
        fun appVersion(view: MoreView): SemanticVersion
    }

    interface Delegate {
        fun onPickCountrySelected()
        fun onDisclaimerSelected()
        fun onPrivacyPolicySelected()
        fun onBookTrainingSelected()
    }

}
