package com.riwal.rentalapp.main.search

import android.content.res.Resources
import com.riwal.rentalapp.R
import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.android.startActivity
import com.riwal.rentalapp.common.extensions.datetime.seconds
import com.riwal.rentalapp.common.extensions.rxjava.debounce
import com.riwal.rentalapp.common.extensions.rxjava.disposedBy
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.helpmechoose.HelpMeChooseWizardController
import com.riwal.rentalapp.machinedetail.MachineDetailController
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.Machine.Type.*
import com.riwal.rentalapp.training.trainingtypes.TrainingTypesViewImpl
import com.riwal.rentalapp.webview.WebViewController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchController(
        val view: SearchView,
        val machinesManager: MachinesManager,
        val countryManager: CountryManager,
        val resources: Resources,
        val analytics: RentalAnalytics
) : ViewLifecycleObserver, SearchView.DataSource, SearchView.Delegate, HelpMeChooseWizardController.Delegate, MachinesManager.Observer, CountryManager.Observer, CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    private val noFilter = MachinesFilter()

    private var activeCountry = countryManager.activeCountry!!
    private var machines: List<Machine> = emptyList()
    private var workingHeightFilterMax = 0
    private var workingOutreachFilterMax = 0
    private var liftCapacityFilterMax = 0
    private var searchResults: List<Machine> = emptyList()
    private val asyncFilter: PublishSubject<MachinesFilter> = PublishSubject.create()
    private val subscriptions = CompositeDisposable()
    private var isSearching = false

    private var machineTypes: List<Machine.Type> = emptyList()
    private var hasFailedLoadingMachines = false
    private var isFiltering = false // TODO: Why is this not in iOS?

    private var filter = noFilter
        set(value) {
            field = value
            view.notifyDataChanged()
            asyncFilter.onNext(value)
        }


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewCreate() {
        super.onViewCreate()

        countryManager.addObserver(this)
        machinesManager.addObserver(this)

        machinesManager.updateMachinesIfNeeded()

        asyncFilter
                .doOnNext {
                    isFiltering = true
                    view.notifyDataChanged()
                }
                .debounce(0.3.seconds())
                .observeOn(Schedulers.computation())
                .map { it.applyTo(machines, resources) }
                .subscribe {
                    searchResults = it
                    isFiltering = false
                    view.notifyDataChanged()
                    analytics.searchResultsShownToUser(filter.query)
                }.disposedBy(subscriptions)
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        machinesManager.removeObserver(this)
        countryManager.removeObserver(this)
        subscriptions.dispose()
        cancel()
    }


    /*---------------------------------- SearchView DataSource -----------------------------------*/


    override fun machineTypes(view: SearchView) = machineTypes
    override fun workingHeightFilterMax(view: SearchView) = workingHeightFilterMax
    override fun workingOutreachFilterMax(view: SearchView) = workingOutreachFilterMax
    override fun liftCapacityFilterMax(view: SearchView) = liftCapacityFilterMax
    override fun filter(view: SearchView) = filter
    override fun searchResults(view: SearchView) = searchResults
    override fun isFiltering(view: SearchView) = isFiltering
    override fun hasFailedLoadingMachines(view: SearchView) = hasFailedLoadingMachines
    override fun activeCountry(view: SearchView) = activeCountry
    override fun isGamingEnabled(): Boolean = activeCountry.isGamingEnabled
    override fun showRentalCodes(): Boolean = activeCountry.name == "NL" || activeCountry.name == "BE"
    override fun showModel(): Boolean  = activeCountry.name == "NL"
    override fun isSearching(view: SearchView) = isSearching


    /*----------------------------------- SearchView Delegate ------------------------------------*/


    override fun onRetryLoadingMachinesSelected(view: SearchView) {
        hasFailedLoadingMachines = false
        machinesManager.updateMachinesIfNeeded()
        view.notifyDataChanged()
    }

    override fun onHelpMeChooseSelected(view: SearchView) {
        navigateToHelpMeChooseWizard()
    }

    override fun onMachineTypeSelected(view: SearchView, machineType: Machine.Type) {
        val localizedName = resources.getString(machineType.localizedNameRes)
        filter = filter.copy(query = localizedName, machineType = machineType)
    }

    override fun onFilterChanged(view: SearchView, filter: MachinesFilter) {
        this.filter = filter
    }

    override fun onSearchResultsShownToUser(view: SearchView) {
        analytics.searchResultsShownToUser(filter.query)
    }

    override fun onMachineSelected(view: SearchView, machine: Machine) {
        navigateToDetailsOf(machine)
    }

    override fun isSearching(view: SearchView, isSearching: Boolean) {
        this.isSearching = isSearching
    }

    override fun onTrainingSelected(view: SearchView) {
        if (activeCountry.trainingFromURL)
            view.navigateToWebView { destination ->
                val controller = destination as WebViewController
                controller.pageTitle = resources.getString(R.string.training)
                controller.pageUrl = activeCountry.trainingURL
            }
        else view.navigateToTrainingTypes()
    }

    override fun onClearSearch() {
        filter = filter.copy(machineType = null)
    }

    override fun didSelectGame(view: SearchView) {
        view.navigateToWebView { destination ->
            val controller = destination as WebViewController
            analytics.adventGameSelected()
            controller.pageTitle = resources.getString(R.string.game_webview_title)
            controller.pageUrl = activeCountry.gamingURL
        }
    }

    override fun onRentalCodesSelected(view: SearchView) {
        view.showPDF("https://www.paperturn-view.com/riwal-holding-group-bv/rwl-tabellen-nieuwecode?pid=MjA205424&v=2")
    }


    /*------------------------------ Help Me Choose Wizard Delegate ------------------------------*/


    override fun onFilterSpecified(controller: HelpMeChooseWizardController, machinesFilter: MachinesFilter) {
        isSearching = true
        filter = machinesFilter
    }


    /*--------------------------------- MachinesManager Observer ---------------------------------*/


    override fun onMachinesUpdated(machinesManager: MachinesManager, machines: List<Machine>) {
        launch {
            preFilterMachinesOnCountry()
            view.notifyDataChanged()
        }
    }

    override fun onMachinesUpdateFailed(machinesManager: MachinesManager, error: Exception) {
        hasFailedLoadingMachines = true
        view.notifyDataChanged()
    }


    /*---------------------------------- CountryManager Observer ---------------------------------*/


    override fun onCountryChanged(countryManager: CountryManager, country: Country) {
        launch {
            activeCountry = country
            machines = machinesManager.updateMachinesIfNeeded()
            machineTypes = emptyList()
            isSearching = false
            view.notifyCountryChanged()
        }
    }


    /*-------------------------------------- Private methods -------------------------------------*/


    private fun preFilterMachinesOnCountry() {

        machines = machinesManager.machines
        machineTypes = machinesManager.machineTypesForCountry(activeCountry)
                .sortedBy { type ->
                    listOf(
                            SCISSOR_LIFT,
                            VERTICAL_LIFT,
                            TELESCOPIC_BOOM_LIFT,
                            ARTICULATED_BOOM_LIFT,
                            SPIDER_LIFT,
                            CRAWLER_LIFT,
                            TRUCK_MOUNTED_LIFT,
                            TRAILER_MOUNTED_LIFT,
                            FORKLIFT,
                            TELEHANDLER_STANDARD,
                            TELEHANDLER_ROTATING,
                            CRANE,
                            GLASS_LIFT
                    ).indexOf(type)
                }

        workingHeightFilterMax = machines.asSequence().map { it.workingHeight.toInt() }.max() ?: 0
        workingOutreachFilterMax = machines.asSequence().map { it.workingOutreach.toInt() }.max()
                ?: 0
        liftCapacityFilterMax = machines.asSequence().map { it.liftCapacity.toInt() }.max() ?: 0

        filter = noFilter
    }

    private fun navigateToHelpMeChooseWizard() {
        view.navigateToHelpMeChooseWizard { destination ->
            destination as HelpMeChooseWizardController
            destination.delegate = this
        }
    }

    private fun navigateToDetailsOf(machine: Machine) {
        analytics.trackDisplayMachineDetailEvent(machine)
        view.navigateToMachineDetailsPage { destination ->
            val controller = destination as MachineDetailController
            controller.machine = machine
        }
    }
}
