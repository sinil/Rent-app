package com.riwal.rentalapp.country

import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.Country
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CountryController(val view: CountryView, val countries: List<Country>, val hasPendingOrder: Boolean) : ViewLifecycleObserver, CountryView.DataSource, CountryView.Delegate, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/


    var delegate: Delegate? = null
    var activeCountry: Country? = null


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this
        observe(view)
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        cancel()
    }


    /*---------------------------- Country View Data Source methods ------------------------------*/


    override fun activeCountry(view: CountryView) = activeCountry
    override fun countries(view: CountryView) = countries


    /*------------------------------ Country View Delegate methods -------------------------------*/


    override fun onNavigateBackSelected() {
        view.navigateBack()
    }

    override fun onCountrySelected(country: Country) {
        launch {
            if (activeCountry != country && hasPendingOrder) {
                if (view.confirmMachineOrdersWillBeCleared()) {
                    countrySelected(country)
                }
            } else {
                countrySelected(country)
            }
        }
    }


    /*------------------------------------ Private methods ---------------------------------------*/


    private fun countrySelected(country: Country) {
        delegate?.onCountrySelected(controller = this, country = country)
        view.navigateBack()
    }


    /*--------------------------------------- Interfaces -----------------------------------------*/


    interface Delegate {
        fun onCountrySelected(controller: CountryController, country: Country)
    }

}