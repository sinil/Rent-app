package com.riwal.rentalapp.accessoriesonrent

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.core.toggle
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.onDateSet
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.range
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.Dialogs
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.model.RentalStatus
import com.riwal.rentalapp.model.RentalStatus.*
import kotlinx.android.synthetic.main.view_my_rentals_search_bar_with_filter_panel.view.*
import org.joda.time.LocalDate

class AccessoriesOnRentSearchBarWithFilterPanal @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, val firstDayOfWeek: Int) : BaseView(context, attrs, defStyleAttr) {


    /*---------------------------------------- Properties ----------------------------------------*/


    var delegate: Delegate? = null

    var filter = AccessoriesOnRentFilter()
    var changableOrderStatuses = RentalStatus.allExceptUnknown
    var canChangePeriod = true

    var isFilterPanelOpen = false
        private set(value) {
            field = value
            updateUI()
            delegate?.onFilterPanelToggled(view = this, isOpen = value)
        }

    var defaultSearchHint = getString(R.string.hint_search)
    var searchHint: String? = null
        set(value) {
            field = value
            updateUI()
        }


    private val orderStatusCheckboxes
        get() = mapOf(
                PENDING_DELIVERY to pendingDeliveryCheckBox,
                ON_RENT to currentRentalsCheckBox,
                PENDING_PICKUP to pendingPickupCheckBox,
                CLOSED to closedRentalsCheckBox
        )


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        addSubview(R.layout.view_my_rentals_search_bar_with_filter_panel)

        searchInput.onEditorActionListener = { actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
            }
        }

        backButton.setOnClickListener { delegate?.onBackButtonPressed(view = this) }
        searchInput.onTextChangedListener = { newQuery -> onSearchQueryChanged(newQuery) }
        searchInput.setOnFocusChangeListener { _, hasFocus -> onSearchInputPressed(hasFocus) }
        clearSearchButton.setOnClickListener { onClearSearchQueryButtonPressed() }
        filterButton.setOnClickListener { onFilterButtonPressed() }

        filterDoneButton.setOnClickListener { onFilterDoneButtonPressed() }
        clearFilterButton.setOnClickListener { onClearFilterButtonPressed() }
        onRentDateButton.setOnClickListener { onOnRentDateButtonPressed() }
        offRentDateButton.setOnClickListener { onOffRentDateButtonPressed() }
        orderStatusCheckboxes.forEach { it.value.setOnCheckedChangeListener { _, _ -> onOrderStatusCheckChanged(it.value) } }
    }


    /*----------------------------------------- Actions ------------------------------------------*/


    private fun onSearchInputPressed(hasFocus: Boolean) {
        if (hasFocus) {
            hideFilterPanel()
        }
    }

    private fun onFilterButtonPressed() {
        searchInput.clearFocusAndHideKeyboard()
        toggleFilterPanel()
    }

    private fun onSearchQueryChanged(newQuery: String) {
        delegate?.onFilterChanged(view = this, newFilter = filter.copy(query = newQuery))
        updateUI()
    }

    private fun onClearSearchQueryButtonPressed() {
        searchInput.text = ""
        searchInput.requestFocus()
        updateUI()
    }

    private fun onOnRentDateButtonPressed() {
        showDatePicker(filter.period.start, max = filter.period.endInclusive) { selectedDate ->
            filter = filter.copy(period = selectedDate..filter.period.endInclusive)
            delegate?.onFilterChanged(view = this, newFilter = filter)
            updateUI()
        }
    }

    private fun onOffRentDateButtonPressed() {
        showDatePicker(filter.period.endInclusive, min = filter.period.start) { selectedDate ->
            filter = filter.copy(period = filter.period.start..selectedDate)
            delegate?.onFilterChanged(view = this, newFilter = filter)
            updateUI()
        }
    }

    private fun onClearFilterButtonPressed() {
        resetOrderStatusCheckBoxes()
        updateUI()
    }

    private fun onFilterDoneButtonPressed() {
        hideFilterPanel()
    }

    private fun onOrderStatusCheckChanged(checkBox: CheckBox) {
        val orderStatus = orderStatusForCheckBox(checkBox)
        filter = filter.copy(selectedRentalStatuses = filter.selectedRentalStatuses.toggle(orderStatus))
        delegate?.onFilterChanged(view = this, newFilter = filter)
        updateUI()
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    fun showFilterPanel() {
        isFilterPanelOpen = true
    }

    fun hideFilterPanel() {
        isFilterPanelOpen = false
    }


    /*------------------------------------- Private methods --------------------------------------*/


    override fun updateUI(animated: Boolean) {

        if (animated) {
            activity.beginDelayedTransition(ParallelAutoTransition())
        }

        val period = filter.period

        filterPanel.isVisible = isFilterPanelOpen

        updateOrderStatusCheckBoxes()

        searchInput.hint = searchHint ?: defaultSearchHint
        clearSearchButton.isVisible = !searchInput.text.isNullOrEmpty()
        clearFilterButton.isVisible = filter.selectedRentalStatuses.isNotEmpty()

        filterPeriodGroup.isVisible = canChangePeriod
        onRentDateButtonTextView.text = period.start.toMediumStyleString()
        offRentDateButtonTextView.text = period.endInclusive.toMediumStyleString()
    }

    private fun toggleFilterPanel() {
        isFilterPanelOpen = !isFilterPanelOpen
    }

    private fun resetOrderStatusCheckBoxes() = orderStatusCheckboxes.forEach { it.value.isChecked = false }

    private fun updateOrderStatusCheckBoxes() = orderStatusCheckboxes.forEach { it.value.isVisible = it.key in changableOrderStatuses }

    private fun orderStatusForCheckBox(checkBox: CheckBox) = orderStatusCheckboxes.filterValues { it == checkBox }.map { it.key }.first()

    private fun showDatePicker(selectedDate: LocalDate, min: LocalDate? = null, max: LocalDate? = null, callback: (date: LocalDate) -> Unit) {
        Dialogs.datePicker(selectedDate, firstDayOfWeek)
                .onDateSet(callback)
                .range(min, max)
                .show(supportFragmentManager, "DatePickerDialog")
    }

    /*---------------------------------------- Interfaces ----------------------------------------*/


    interface Delegate {
        fun onBackButtonPressed(view: AccessoriesOnRentSearchBarWithFilterPanal)
        fun onFilterPanelToggled(view: AccessoriesOnRentSearchBarWithFilterPanal, isOpen: Boolean)
        fun onFilterChanged(view: AccessoriesOnRentSearchBarWithFilterPanal, newFilter: AccessoriesOnRentFilter)
    }
}