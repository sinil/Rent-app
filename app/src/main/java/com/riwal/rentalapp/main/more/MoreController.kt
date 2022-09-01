package com.riwal.rentalapp.main.more

import android.content.res.Resources
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.country.CountryController
import com.riwal.rentalapp.model.AppVersionManager
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.CountryManager
import com.riwal.rentalapp.model.OrderManager
import com.riwal.rentalapp.webview.WebViewController

class MoreController(
        val view: MoreView,
        val countryManager: CountryManager,
        val orderManager: OrderManager,
        val appVersionManager: AppVersionManager,
        val resources: Resources) : ViewLifecycleObserver, MoreView.DataSource, MoreView.Delegate, CountryController.Delegate {


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }


    /*-------------------------------------- View DataSource -------------------------------------*/


    override fun country(view: MoreView) = countryManager.activeCountry!!
    override fun appVersion(view: MoreView) = appVersionManager.installedAppVersion


    /*--------------------------------------- View Delegate --------------------------------------*/


    override fun onPickCountrySelected() {
        view.navigateToPickCountryPage { destination ->
            val controller = destination as CountryController
            controller.delegate = this
            controller.activeCountry = countryManager.activeCountry
        }
    }

    override fun onDisclaimerSelected() {
        view.navigateToWebView { destination ->
            val controller = destination as WebViewController
            controller.pageTitle = resources.getString(R.string.setting_disclaimer)
            controller.pageUrl = resources.getString(R.string.disclaimer_url)
        }
    }

    override fun onPrivacyPolicySelected() {
        view.navigateToWebView { destination ->
            val controller = destination as WebViewController
            controller.pageTitle = resources.getString(R.string.setting_privacy_policy)
            controller.pageUrl = resources.getString(R.string.privacy_policy_url)
        }
    }

    override fun onBookTrainingSelected() {
        if (countryManager.activeCountry?.trainingFromURL!!)
            view.navigateToPlusPortWebView { destination ->
                val controller = destination as WebViewController
                controller.pageTitle = resources.getString(R.string.training)
                controller.pageUrl = countryManager.activeCountry?.trainingURL!!
            }
        else view.navigateToBookTrainingPage()
    }


    /*-------------------------------- CountryController Delegate --------------------------------*/


    override fun onCountrySelected(controller: CountryController, country: Country) {
        if (country != countryManager.activeCountry) {
            orderManager.removeAllMachinesFromCurrentOrder()
            countryManager.activeCountry = country
            view.notifyDataChanged()
        }
    }

}