package com.riwal.rentalapp.helpmechoose

import android.content.res.Resources
import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.core.roundDown
import com.riwal.rentalapp.common.extensions.datetime.seconds
import com.riwal.rentalapp.common.extensions.rxjava.debounce
import com.riwal.rentalapp.common.extensions.rxjava.disposedBy
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.helpmechoose.LiftType.MATERIALS
import com.riwal.rentalapp.helpmechoose.LiftType.PEOPLE
import com.riwal.rentalapp.helpmechoose.Location.NOT_SPECIFIED
import com.riwal.rentalapp.helpmechoose.ReachType.ANGLED
import com.riwal.rentalapp.helpmechoose.ReachType.MULTI_ANGLED
import com.riwal.rentalapp.main.search.MachinesFilter
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Machine
import com.riwal.rentalapp.model.MachinesManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class HelpMeChooseWizardController(
        val view: HelpMeChooseWizardView,
        val activeCountry: Country,
        val machinesManager: MachinesManager,
        val resources: Resources,
        val analytics: RentalAnalytics
) : HelpMeChooseWizardView.DataSource, HelpMeChooseWizardView.Delegate, ViewLifecycleObserver {


    /*--------------------------------------- Properties -----------------------------------------*/


    var delegate: Delegate? = null


    private val availableMachinesForActiveCountry = machinesManager.updateMachinesIfNeeded()
    private val availableMachineTypesForActiveCountry = machinesManager.machineTypesForCountry(activeCountry)
    private val asyncFilter: PublishSubject<FilterAndInput> = PublishSubject.create()
    private var subscriptions = CompositeDisposable()
    private var matchingMachines = availableMachinesForActiveCountry

    private var workSpecification = WorkSpecification()
        set(value) {
            field = value
            filterMachines()
            view.notifyDataChanged()
        }

    private val machinesFilter
        get() = workSpecification.toMachinesFilter(availableMachines = availableMachineTypesForActiveCountry, resources = resources)

    private val canSelectLocation
        get() = liftType != LiftType.NOT_SPECIFIED

    private val canSelectReach
        get() = liftType == PEOPLE && location != NOT_SPECIFIED

    private val canSelectWorkingHeight
        get() = (reachType != ReachType.NOT_SPECIFIED || liftType == MATERIALS && location != NOT_SPECIFIED) && maximumWorkingHeight > 0

    private val canSelectWorkingOutreach
        get() = (reachType in listOf(ANGLED, MULTI_ANGLED) || liftType == MATERIALS && location != NOT_SPECIFIED) && maximumWorkingOutreach > 0

    private val canShowResults
        get() = (reachType != ReachType.NOT_SPECIFIED || liftType == MATERIALS && location != NOT_SPECIFIED) && numberOfResults > 0

    private val liftType
        get() = workSpecification.liftType

    private val location
        get() = workSpecification.location

    private val reachType
        get() = workSpecification.reachType

    private val minimumWorkingHeight
        get() = workSpecification.minimumWorkingHeight

    private val minimumWorkingOutreach
        get() = workSpecification.minimumWorkingOutreach

    private val maximumWorkingHeight
        get() = matchingMachines.map { it.workingHeight.roundDown().toInt() }.max() ?: 0

    private val maximumWorkingOutreach
        get() = matchingMachines.map { it.workingOutreach.roundDown().toInt() }.max() ?: 0

    private val matchingMachineTypesForCountry by lazy {
        availableMachinesForActiveCountry
                .map { it.type }
                .toSet()
                .intersect(workSpecification.matchingMachineTypes)
                .toList()
    }

    private val numberOfResults
        get() = matchingMachines.count()


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewCreate() {
        super.onViewCreate()
        setUpAsyncMachinesFilter()
    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.userLookingAtHelpMeChoosePage()
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    private fun setUpAsyncMachinesFilter() {
        asyncFilter
                .debounce(0.3.seconds())
                .observeOn(Schedulers.computation())
                .map { it.filter.applyTo(it.input, resources = resources) }
                .subscribe {
                    matchingMachines = it
                    view.notifyDataChanged()
                }
                .disposedBy(subscriptions)
    }


    /*---------------------------------------- DataSource ----------------------------------------*/


    override fun canSelectLocation(view: HelpMeChooseWizardView) = canSelectLocation
    override fun canSelectReachType(view: HelpMeChooseWizardView) = canSelectReach
    override fun canSelectWorkingHeight(view: HelpMeChooseWizardView) = canSelectWorkingHeight
    override fun canSelectWorkingOutreach(view: HelpMeChooseWizardView) = canSelectWorkingOutreach
    override fun canShowResults(view: HelpMeChooseWizardView) = canShowResults
    override fun liftType(view: HelpMeChooseWizardView) = liftType
    override fun location(view: HelpMeChooseWizardView) = location
    override fun reachType(view: HelpMeChooseWizardView) = reachType
    override fun minimumWorkingHeight(view: HelpMeChooseWizardView) = minimumWorkingHeight
    override fun minimumWorkingOutreach(view: HelpMeChooseWizardView) = minimumWorkingOutreach
    override fun maximumWorkingHeight(view: HelpMeChooseWizardView) = maximumWorkingHeight
    override fun maximumWorkingOutreach(view: HelpMeChooseWizardView) = maximumWorkingOutreach
    override fun numberOfResults(view: HelpMeChooseWizardView) = numberOfResults


    /*----------------------------------------- Delegate -----------------------------------------*/


    override fun onLiftTypeSelected(view: HelpMeChooseWizardView, liftType: LiftType) {
        workSpecification = WorkSpecification(liftType = liftType)
    }

    override fun onLocationSelected(view: HelpMeChooseWizardView, location: Location) {
        workSpecification = WorkSpecification(liftType = workSpecification.liftType, location = location)
    }

    override fun onReachTypeSelected(view: HelpMeChooseWizardView, reachType: ReachType) {
        workSpecification = WorkSpecification(liftType = workSpecification.liftType, location = workSpecification.location, reachType = reachType)
    }

    override fun onMinimumWorkingHeightChanged(view: HelpMeChooseWizardView, newValue: Int) {
        workSpecification = workSpecification.copy(minimumWorkingHeight = newValue)
    }

    override fun onMinimumWorkingOutreachChanged(view: HelpMeChooseWizardView, newValue: Int) {
        workSpecification = workSpecification.copy(minimumWorkingOutreach = newValue)
    }

    override fun onShowResultsSelected(view: HelpMeChooseWizardView) {
        analytics.helpMeChooseResultsSelected()
        delegate?.onFilterSpecified(controller = this, machinesFilter = machinesFilter)
        view.navigateBack()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun filterMachines() {
        val filterAndInput = FilterAndInput(filter = machinesFilter, input = availableMachinesForActiveCountry)
        asyncFilter.onNext(filterAndInput)
    }


    /*--------------------------------------- Interfaces -----------------------------------------*/


    interface Delegate {
        fun onFilterSpecified(controller: HelpMeChooseWizardController, machinesFilter: MachinesFilter)
    }

    data class FilterAndInput(
            val filter: MachinesFilter,
            val input: List<Machine>
    )
}
