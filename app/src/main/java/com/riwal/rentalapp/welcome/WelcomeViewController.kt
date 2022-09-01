package com.riwal.rentalapp.welcome

import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.country.CountryController
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.CountryManager

class WelcomeViewController(val view: WelcomeView, val countryManager: CountryManager) : ViewLifecycleObserver, WelcomeView.DataSource, WelcomeView.Delegate, CountryController.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    private var selectedCountry = countryManager.activeCountry


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewCreate() {
        super.onViewCreate()

        if (countryManager.isCountrySelected) {
            view.navigateToMainPage()
        }
    }


    /*---------------------------- Welcome View Data Source methods ------------------------------*/


    override fun activeCountry(view: WelcomeView) = selectedCountry
    override fun canContinue(view: WelcomeView) = selectedCountry != null


    /*------------------------------ Welcome View Delegate methods -------------------------------*/


    override fun onPickCountrySelected() {
        view.navigateToPickCountryPage { destination ->
            val controller = destination as CountryController
            controller.delegate = this
            controller.activeCountry = selectedCountry
        }
    }

    override fun onContinueSelected() {
        countryManager.activeCountry = selectedCountry
        view.navigateToMainPage()
    }


    /*-------------------------------- CountryController Delegate --------------------------------*/


    override fun onCountrySelected(controller: CountryController, country: Country) {
        selectedCountry = country
        view.notifyDataChanged()
    }


}