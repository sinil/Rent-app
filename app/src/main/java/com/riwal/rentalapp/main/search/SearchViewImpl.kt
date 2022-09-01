package com.riwal.rentalapp.main.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import com.codertainment.materialintro.shape.Focus
import com.codertainment.materialintro.shape.ShapeType
import com.codertainment.materialintro.utils.materialIntro
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.core.format
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.BadgeView
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.contactinfo.ContactInfoViewImpl
import com.riwal.rentalapp.helpmechoose.HelpMeChooseWizardViewImpl
import com.riwal.rentalapp.machinedetail.MachineDetailViewImpl
import com.riwal.rentalapp.main.MainViewImpl
import com.riwal.rentalapp.model.Machine
import com.riwal.rentalapp.training.trainingtypes.TrainingTypesViewImpl
import com.riwal.rentalapp.webview.WebViewImpl
import kotlinx.android.synthetic.main.view_search.view.*


class SearchViewImpl @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseView(context, attrs, defStyleAttr), SearchView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: SearchView.DataSource
    override lateinit var delegate: SearchView.Delegate

    private var searchResults: List<Machine> = emptyList()
    private var previousSearchResults = emptyList<Machine>()
    private var isFilterPanelOpen = false

    private val isSearching
        get() = dataSource.isSearching(view = this)

    private val categories
        get() = machineTypes.map { MachineCategory(it) }

    private val machineTypes
        get() = dataSource.machineTypes(view = this)

    private val filter
        get() = dataSource.filter(view = this)

    private val isFilterPanelVisible
        get() = filterPanel?.isVisible ?: false

    private val isWorkingHeightFilterActive
        get() = workingHeightSlider.value > 0

    private val isWorkingOutreachFilterActive
        get() = workingOutreachSlider.value > 0

    private val isLiftCapacityFilterActive
        get() = liftCapacitySlider.value > 0

    private val isPropulsionFilterActive: Boolean
        get() = listOf(electricPropulsionCheckbox, fossilFuelPropulsionCheckbox, hybridPropulsionCheckbox).any { it.isChecked }

    private val maximumWorkingHeight
        get() = dataSource.workingHeightFilterMax(view = this)

    private val maximumWorkingOutreach
        get() = dataSource.workingOutreachFilterMax(view = this)

    private val maximumLiftCapacity
        get() = dataSource.liftCapacityFilterMax(view = this)

    private val isFiltering
        get() = dataSource.isFiltering(view = this)

    private val hasFailedLoadingMachines
        get() = dataSource.hasFailedLoadingMachines(view = this)

    private val activeCountry
        get() = dataSource.activeCountry(view = this)

    private val isGameEnabled
        get() = dataSource.isGamingEnabled()

    private val showRentalCode
        get() = dataSource.showRentalCodes()

    private val showModel
        get() = dataSource.showModel()

    private var isTutorialDisplayed = false

    private lateinit var badge: BadgeView


    /*----------------------------------------- Lifecycle ----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        addSubview(R.layout.view_search)

        machinesRecyclerView.dataSource = this
        machinesRecyclerView.delegate = this

        searchCategoriesRecyclerView.dataSource = this
        searchCategoriesRecyclerView.delegate = this

        searchButton.setOnClickListener { searchInput.requestFocusAndShowKeyboard() }
        backButton.setOnClickListener { onBackPressed() }

        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                delegate.isSearching(view = this, isSearching = true)
                showFilterPanel(false)
            }
            updateUI()
        }
        searchInput.onTextChangedListener = { delegate.onFilterChanged(view = this, filter = filter.copy(query = it)) }

        workingHeightSlider.setOnValueChangedListener { delegate.onFilterChanged(view = this, filter = filter.copy(minimumWorkingHeight = it)) }
        workingOutreachSlider.setOnValueChangedListener { delegate.onFilterChanged(view = this, filter = filter.copy(minimumWorkingOutreach = it)) }
        liftCapacitySlider.setOnValueChangedListener { delegate.onFilterChanged(view = this, filter = filter.copy(minimumLiftCapacity = it)) }

        electricPropulsionCheckbox.setOnCheckedChangeListener { _, checked -> delegate.onFilterChanged(view = this, filter = filter.copy(electricPropulsion = checked)) }
        fossilFuelPropulsionCheckbox.setOnCheckedChangeListener { _, checked -> delegate.onFilterChanged(view = this, filter = filter.copy(fossilFuelPropulsion = checked)) }
        hybridPropulsionCheckbox.setOnCheckedChangeListener { _, checked -> delegate.onFilterChanged(view = this, filter = filter.copy(hybridPropulsion = checked)) }

        filterPanelOverlay.setOnClickListener { showFilterPanel(false) }
        filterDoneButton.setOnClickListener { showFilterPanel(false) }
        clearFilterButton.setOnClickListener { clearFilter() }

        filterButton.setOnClickListener { searchInput.clearFocusAndHideKeyboard(); toggleFilterPanel() }
        clearSearchButton.setOnClickListener { clearSearch() }

        retryLoadingMachinesButton.setOnClickListener { delegate.onRetryLoadingMachinesSelected(view = this) }

        helpMeChooseLayout.setOnClickListener { delegate.onHelpMeChooseSelected(view = this) }

        trainingLayout.setOnClickListener {
            delegate.onTrainingSelected(this)
        }

        rentalCodesLayout.setOnClickListener { delegate.onRentalCodesSelected(this) }

        gameLayout.setOnClickListener {
            delegate.didSelectGame(this)
        }

        badge = BadgeView(context)
        badge.text = activity.getString(R.string.string_new)

        gameLayout.addView(badge)

        updateUI(animated = false)


    }

    private fun showMaterialIntro() {
        tutorialLayer.visibility = View.VISIBLE
        (activity as MainViewImpl).toggleTutorialLayerVisibility(true)

        activity.materialIntro(true) {
            focusType = Focus.NORMAL
            titleTex = activity.getString(R.string.new_feature_alert)
            infoText = if (activeCountry.trainingFromURL)
                activity.getString(R.string.training_tutorial_message_home_from_url)
            else
                activity.getString(R.string.training_tutorial_message_home)
            okText = activity.getString(R.string.button_ok)
            skipText = activity.getString(R.string.hide_tutorial_label)
            isHideButton = true
            shapeType = ShapeType.CIRCLE
            viewId = "trainingImageView"
            targetView = this@SearchViewImpl.trainingImageView
            parentLayerView = (activity as MainViewImpl).tutorialLayer
            layerView = this@SearchViewImpl.tutorialLayer
            clickAction = {
                this@SearchViewImpl.tutorialLayer.visibility = View.GONE
                (activity as MainViewImpl).toggleTutorialLayerVisibility(false)
                delegate.onTrainingSelected(this@SearchViewImpl)
            }
            isTutorialDisplayed = true

        }
    }

    override fun onBackPressed(): Boolean {
        return when {
            isFilterPanelVisible -> {
                showFilterPanel(false)
                true
            }
            isSearching -> {
                delegate.isSearching(view = this, isSearching = false)
                delegate.onClearSearch()
                searchInput.clearFocusAndHideKeyboard()
                clearSearch()
                clearFilter()
                updateUI()
                true
            }
            else -> false
        }
    }


    /*----------------------------------- Search View methods ------------------------------------*/


    override fun notifyCountryChanged() = runOnUiThread {
        delegate.isSearching(view = this, isSearching = false)
        showFilterPanel(false)
        clearFilter()
        clearSearch()
        updateUI()
    }

    override fun navigateToHelpMeChooseWizard(preparationHandler: ControllerPreparationHandler) {
        startActivity(HelpMeChooseWizardViewImpl::class, preparationHandler)
    }

    override fun navigateToMachineDetailsPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(MachineDetailViewImpl::class, preparationHandler)
    }

    override fun navigateToWebView(preparationHandler: ControllerPreparationHandler) {
        startActivity(WebViewImpl::class, preparationHandler)
    }

    override fun navigateToTrainingTypes() {
        startActivity(TrainingTypesViewImpl::class)
    }

    override fun showPDF(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }


    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int): Int {
        return if (recyclerView == searchCategoriesRecyclerView) categories.size else searchResults.size
    }

    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath): Int {
        return when (recyclerView) {
            searchCategoriesRecyclerView -> R.layout.row_machine_type
            machinesRecyclerView -> R.layout.row_machine
            else -> throw IllegalStateException()
        }
    }

    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int): EasyRecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.row_machine_type -> SearchCategoryRowViewHolder(itemView)
            R.layout.row_machine -> MachineRowViewHolder(itemView)
            else -> throw IllegalStateException()
        }
    }


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {

        when (viewHolder) {
            is SearchCategoryRowViewHolder -> {
                val category = categories[indexPath.row]
                viewHolder.updateWith(category)
            }
            is MachineRowViewHolder -> {
                val machine = searchResults[indexPath.row]
                viewHolder.modelTextView
                viewHolder.showModelField = showModel
                viewHolder.updateWith(machine)
            }
        }
    }

    override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        if (recyclerView == searchCategoriesRecyclerView) {
            val category = categories[indexPath.row]
            onCategorySelected(category)
        } else {
            val machine = searchResults[indexPath.row]
            onMachineSelected(machine)
        }
    }

    private fun onCategorySelected(category: Category) {
        delegate.isSearching(view = this, isSearching = true)
        when (category) {
//            is HelpMeChooseCategory -> delegate.onHelpMeChooseSelected(view = this)
            is MachineCategory -> delegate.onMachineTypeSelected(view = this, machineType = category.machineType)
        }
    }

    private fun onMachineSelected(machine: Machine) {
        showFilterPanel(false, animated = false)
        delegate.onMachineSelected(view = this, machine = machine)
    }


    /*-------------------------------------- Private methods -------------------------------------*/


    private fun showFilterPanel(show: Boolean = true, animated: Boolean = true) {
        if (isFilterPanelOpen != show) {
            isFilterPanelOpen = show
            updateUI(animated)
        }
    }

    private fun toggleFilterPanel(animated: Boolean = true) {
        showFilterPanel(!isFilterPanelOpen, animated)
    }

    private fun clearFilter() {
        workingHeightSlider.value = 0
        workingOutreachSlider.value = 0
        liftCapacitySlider.value = 0
        electricPropulsionCheckbox.isChecked = false
        fossilFuelPropulsionCheckbox.isChecked = false
        hybridPropulsionCheckbox.isChecked = false
    }

    private fun clearSearch() {
        searchInput.setText("")
    }

    @SuppressLint("StringFormatMatches")
    override fun updateUI(animated: Boolean) {

        rentalCodesLayout.isVisible = showRentalCode

        searchResults = dataSource.searchResults(view = this)

        if (searchResults != previousSearchResults) {
            previousSearchResults = searchResults
            machinesRecyclerView.scrollToPosition(0)
            machinesRecyclerView.notifyDataSetChanged()
        }

        searchCategoriesRecyclerView.notifyDataSetChanged()

        if (animated) {
            val transition = ParallelAutoTransition()
                    .excludeTarget(searchCategoriesRecyclerView, true)
                    .excludeTarget(searchInput, true)
            beginDelayedTransition(transition)
        }

        val hasMachineTypes = categories.filterIsInstance<MachineCategory>().isNotEmpty()
        val isLoading = !hasMachineTypes
        activityIndicator.isVisible = !hasFailedLoadingMachines && isLoading
        featureLayout.isVisible = !isLoading && !isSearching
        helpMeChooseLayout.isVisible = !isLoading && !hasFailedLoadingMachines
        searchCategoriesRecyclerView.isVisible = !isLoading && !isSearching

        val query = filter.query
        val isFilterActive = (isWorkingHeightFilterActive || isWorkingOutreachFilterActive || isLiftCapacityFilterActive || isPropulsionFilterActive)
        clearSearchButton.isVisible = query.isNotEmpty()
        clearFilterButton.isVisible = isFilterActive

        searchButton.isVisible = !isSearching
        backButton.isVisible = isSearching
        toolbarView.isVisible = !isSearching
        toolbarView.elevation = if (isSearching) 0f else dp(4f)
        titleTextView.isVisible = !isSearching
        searchResultsTextView.isVisible = isSearching && !isFiltering && searchResults.isNotEmpty()
        searchResultsTextView.text = getString(R.string.search_results, searchResults.size)
        machinesRecyclerView.isVisible = isSearching && !isFiltering && searchResults.isNotEmpty()
        retryLoadingMachinesContainer.isVisible = hasFailedLoadingMachines
        noMatchingMachinesView.isVisible = isSearching && !isFiltering && searchResults.isEmpty()

        electricPropulsionCheckbox.isChecked = filter.electricPropulsion
        fossilFuelPropulsionCheckbox.isChecked = filter.fossilFuelPropulsion
        hybridPropulsionCheckbox.isChecked = filter.hybridPropulsion

        if (searchInput.text.toString() != filter.query) {
            searchInput.setTextKeepState(filter.query)
        }

        if (activeCountry.isBookingTrainingEnabled) {
            trainingLayout.visibility = View.VISIBLE
            Handler().postDelayed({
                if (!isTutorialDisplayed && !hasFailedLoadingMachines && !isLoading)
                    showMaterialIntro()

            }, 800)

        } else {
            trainingLayout.visibility = View.GONE

        }

        gameLayout.isVisible = isGameEnabled


        filterPanel.isVisible = isFilterPanelOpen
        filterPanelOverlay.isVisible = isFilterPanelOpen
        filterButton.isVisible = isSearching
        filterButton.setTextColor(if (isFilterActive) resources.getColor(R.color.colorAccent) else resources.getColor(R.color.black))

        workingHeightSlider.value = filter.minimumWorkingHeight
        workingHeightSlider.max = maximumWorkingHeight

        workingOutreachSlider.value = filter.minimumWorkingOutreach
        workingOutreachSlider.max = maximumWorkingOutreach

        liftCapacitySlider.value = filter.minimumLiftCapacity
        liftCapacitySlider.max = maximumLiftCapacity

        minimumWorkingHeightTextView.text = context.getString(R.string.value_in_meters, filter.minimumWorkingHeight)
        minimumWorkingOutreachTextView.text = context.getString(R.string.value_in_meters, filter.minimumWorkingOutreach)
        minimumLiftCapacityTextView.text = context.getString(R.string.value_in_kg, filter.minimumLiftCapacity.format())


    }

}

sealed class Category
object HelpMeChooseCategory : Category()
class MachineCategory(val machineType: Machine.Type) : Category()