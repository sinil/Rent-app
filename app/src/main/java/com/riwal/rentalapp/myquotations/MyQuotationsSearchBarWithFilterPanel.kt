package com.riwal.rentalapp.myquotations

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.View
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
import com.riwal.rentalapp.model.Contact
import com.riwal.rentalapp.model.VenueBody
import kotlinx.android.synthetic.main.view_my_quotations_search_bar_with_filter_panel.view.*
import kotlinx.android.synthetic.main.view_my_quotations_search_bar_with_filter_panel.view.backButton
import kotlinx.android.synthetic.main.view_my_quotations_search_bar_with_filter_panel.view.clearFilterButton
import kotlinx.android.synthetic.main.view_my_quotations_search_bar_with_filter_panel.view.clearSearchButton
import kotlinx.android.synthetic.main.view_my_quotations_search_bar_with_filter_panel.view.endDateButton
import kotlinx.android.synthetic.main.view_my_quotations_search_bar_with_filter_panel.view.filterButton
import kotlinx.android.synthetic.main.view_my_quotations_search_bar_with_filter_panel.view.filterDoneButton
import kotlinx.android.synthetic.main.view_my_quotations_search_bar_with_filter_panel.view.filterPanel
import kotlinx.android.synthetic.main.view_my_quotations_search_bar_with_filter_panel.view.searchInput
import kotlinx.android.synthetic.main.view_my_quotations_search_bar_with_filter_panel.view.startDateButton
import org.joda.time.LocalDate


class MyQuotationsSearchBarWithFilterPanel @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, val firstDayOfWeek: Int) : BaseView(context, attrs, defStyleAttr) {


    /*---------------------------------------- Properties ----------------------------------------*/

    var delegate: Delegate? = null

    var filter = MyQuotationsFilter()

    var isFilterPanelOpen = false
        private set(value) {
            field = value
            updateUI()
            delegate?.onFilterPanelToggled(view = this, isOpen = value)
        }

    private var contactsCheckboxes = ArrayList<CheckBox>()

    private var venuesCheckboxes = ArrayList<CheckBox>()

    /*---------------------------------------- Lifecycle -----------------------------------------*/

    override fun onCreate() {
        super.onCreate()
        addSubview(R.layout.view_my_quotations_search_bar_with_filter_panel)

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
        startDateButton.setOnClickListener { onDateButtonPressed() }
        endDateButton.setOnClickListener { onDueDateButtonPressed() }
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

    private fun onDateButtonPressed() {
        showDatePicker(filter.period.start, max = filter.period.endInclusive) { selectedDate ->
            filter = filter.copy(period = selectedDate..filter.period.endInclusive)
            delegate?.onFilterChanged(view = this, newFilter = filter.copy(period = selectedDate..filter.period.endInclusive))
            updateUI()
        }
    }

    private fun onDueDateButtonPressed() {
        showDatePicker(filter.period.endInclusive, min = filter.period.start) { selectedDate ->
            filter = filter.copy(period = filter.period.start..selectedDate)
            delegate?.onFilterChanged(view = this, newFilter = filter.copy(period = filter.period.start..selectedDate))
            updateUI()
        }
    }

    private fun onClearFilterButtonPressed() {
        resetContactsCheckBoxes()
        resetVenuesCheckBoxes()
        updateUI()
    }

    private fun onFilterDoneButtonPressed() {
        hideFilterPanel()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    fun populateVenues(venues: List<VenueBody?>) {
        Handler().postDelayed({

            if (venues.isNotEmpty()) {
                venueTitleTextView.visibility = View.GONE
                venuesLayout.removeAllViews()
                venuesCheckboxes.clear()
            }

            filter.relevantVenues = venues.map { it?.city }


            for (i in venues.indices) {
                if (venues[i]?.code != null && venues[i]?.city != null) {
                    venueTitleTextView.visibility = View.VISIBLE

                    val checkBox = CheckBox(context)
                    checkBox.id = id
                    checkBox.text = venues[i]?.city
                    checkBox.tag = venues[i]?.city

                    venuesCheckboxes.add(checkBox)
                    venuesLayout.addView(checkBox)

                    checkBox.setOnCheckedChangeListener { _, _ -> onVenueCheckChanged(checkBox) }

                }
            }
        }, 1000)
    }

    fun populateContacts(contacts: List<Contact?>) {
        Handler().postDelayed({
            filter.relevantContacts = contacts.map { it?.name }
            if (contacts.isNotEmpty()) {
                contactTitleTextView.visibility = View.GONE
                contactsLayout.removeAllViews()
                contactsCheckboxes.clear()
            }

            for (i in contacts.indices) {
                if (contacts[i]?.id != null) {
                    contactTitleTextView.visibility = View.VISIBLE

                    val checkBox = CheckBox(context)
                    checkBox.id = id
                    checkBox.text = contacts[i]?.name
                    checkBox.tag = contacts[i]?.name

                    contactsCheckboxes.add(checkBox)
                    contactsLayout.addView(checkBox)

                    checkBox.setOnCheckedChangeListener { _, _ -> onContactCheckChanged(checkBox) }
                }

            }
        }, 1000)
    }


    override fun updateUI(animated: Boolean) {

        if (animated) {
            activity.beginDelayedTransition(ParallelAutoTransition())
        }

        val period = filter.period
        filterPanel.isVisible = isFilterPanelOpen

        clearSearchButton.isVisible = !searchInput.text.isNullOrEmpty()
        clearFilterButton.isVisible = filter.selectedPaid.isNotEmpty() || filter.selectedContacts.isNotEmpty() || filter.selectedVenue.isNotEmpty()

        quotationDateButtonTextView.text = period.start.toMediumStyleString()
        quotationDueDateButtonTextView.text = period.endInclusive.toMediumStyleString()


    }

    private fun toggleFilterPanel() {
        isFilterPanelOpen = !isFilterPanelOpen
    }

    private fun resetContactsCheckBoxes() = contactsCheckboxes.forEach { it.isChecked = false }

    private fun resetVenuesCheckBoxes() = venuesCheckboxes.forEach { it.isChecked = false }

    private fun showDatePicker(selectedDate: LocalDate, min: LocalDate? = null, max: LocalDate? = null, callback: (date: LocalDate) -> Unit) {
        Dialogs.datePicker(selectedDate, firstDayOfWeek)
                .onDateSet(callback)
                .range(min, max)
                .show(supportFragmentManager, "DatePickerDialog")
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    private fun onContactCheckChanged(checkBox: CheckBox) {
        filter = filter.copy(selectedContacts = filter.selectedContacts.toggle(checkBox.tag as String))
        delegate?.onFilterChanged(view = this, newFilter = filter)
        updateUI()
    }

    private fun onVenueCheckChanged(checkBox: CheckBox) {
        filter = filter.copy(selectedVenue = filter.selectedVenue.toggle(checkBox.tag as String))
        delegate?.onFilterChanged(view = this, newFilter = filter)
        updateUI()
    }


    fun showFilterPanel() {
        isFilterPanelOpen = true
    }

    fun hideFilterPanel() {
        isFilterPanelOpen = false
    }

    /*---------------------------------------- Interfaces ----------------------------------------*/


    interface Delegate {
        fun onBackButtonPressed(view: MyQuotationsSearchBarWithFilterPanel)
        fun onFilterPanelToggled(view: MyQuotationsSearchBarWithFilterPanel, isOpen: Boolean)
        fun onFilterChanged(view: MyQuotationsSearchBarWithFilterPanel, newFilter: MyQuotationsFilter)
    }
}


