package com.riwal.rentalapp.country

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Country

interface CountryView : MvcView, ObservableLifecycleView {

    var delegate: Delegate
    var dataSource: DataSource

    suspend fun confirmMachineOrdersWillBeCleared(): Boolean
    fun navigateBack()

    interface DataSource {
        fun activeCountry(view: CountryView): Country?
        fun countries(view: CountryView): List<Country>
    }

    interface Delegate {
        fun onNavigateBackSelected()
        fun onCountrySelected(country: Country)
    }
}
