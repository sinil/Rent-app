package com.riwal.rentalapp.main.search

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Machine

interface SearchView : MvcView, ObservableLifecycleView {

    var delegate: Delegate
    var dataSource: DataSource

    fun notifyCountryChanged()
    fun navigateToHelpMeChooseWizard(preparationHandler: ControllerPreparationHandler)
    fun navigateToMachineDetailsPage(preparationHandler: ControllerPreparationHandler)
    fun navigateToWebView(preparationHandler: ControllerPreparationHandler)
    fun navigateToTrainingTypes()
    fun showPDF(url: String)

    interface DataSource {
        fun machineTypes(view: SearchView): List<Machine.Type>
        fun workingHeightFilterMax(view: SearchView): Int
        fun workingOutreachFilterMax(view: SearchView): Int
        fun liftCapacityFilterMax(view: SearchView): Int
        fun filter(view: SearchView): MachinesFilter
        fun searchResults(view: SearchView): List<Machine>
        fun isFiltering(view: SearchView): Boolean
        fun isSearching(view: SearchView): Boolean
        fun hasFailedLoadingMachines(view: SearchView): Boolean
        fun activeCountry(view: SearchView): Country
        fun isGamingEnabled(): Boolean
        fun showRentalCodes(): Boolean
        fun showModel(): Boolean
    }

    interface Delegate {
        fun onRetryLoadingMachinesSelected(view: SearchView)
        fun onHelpMeChooseSelected(view: SearchView)
        fun onMachineTypeSelected(view: SearchView, machineType: Machine.Type)
        fun onFilterChanged(view: SearchView, filter: MachinesFilter)
        fun onSearchResultsShownToUser(view: SearchView)
        fun onMachineSelected(view: SearchView, machine: Machine)
        fun isSearching(view: SearchView, isSearching: Boolean)
        fun onTrainingSelected(view: SearchView)
        fun onClearSearch()
        fun didSelectGame(view: SearchView)
        fun onRentalCodesSelected(view: SearchView)
    }

}