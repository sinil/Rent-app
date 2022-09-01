package com.riwal.rentalapp.myinvoices

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
import com.riwal.rentalapp.model.InvoiceTypes
import com.riwal.rentalapp.model.VenueBody
import kotlinx.android.synthetic.main.view_my_invoices_search_bar_with_filter_panel.view.*
import org.joda.time.LocalDate


class MyInvoicesSearchBarWithFilterPanel @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, val firstDayOfWeek: Int) : BaseView(context, attrs, defStyleAttr) {


    /*---------------------------------------- Properties ----------------------------------------*/


    var delegate: Delegate? = null


    var filter = MyInvoicesFilter()

    var isFilterPanelOpen = false
        private set(value) {
            field = value
            updateUI()
            delegate?.onFilterPanelToggled(view = this, isOpen = value)
        }

    private val invoiceTypeCheckboxes
        get() = mapOf(
                InvoiceTypes.SALES to salesCheckBox,
                InvoiceTypes.RENTAL to rentalCheckBox,
                InvoiceTypes.MAINTENANCE to maintenanceCheckBox,
                InvoiceTypes.DAMAGE to damageCheckBox,
                InvoiceTypes.TRAINING to trainingCheckBox,
                InvoiceTypes.CREDIT to creditCheckBox,
                InvoiceTypes.HIRE_CREDIT to hireCreditCheckBox,
                InvoiceTypes.REPAIR to repairCheckBox,
                InvoiceTypes.LOSS to lossCheckBox

        )

    private var selectedPaidCheckBox = mutableListOf<Boolean>()
    private var selectedOverdueCheckBox = mutableListOf<Boolean>()

    private val paidCheckboxes
        get() = arrayOf(yesPaidCheckBox, noPaidCheckBox)


    private val overdueCheckboxes
        get() = arrayOf(yesOverDueCheckBox, noOverDueCheckBox)

    private var contactsCheckboxes = ArrayList<CheckBox>()

    private var venuesCheckboxes = ArrayList<CheckBox>()

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        addSubview(R.layout.view_my_invoices_search_bar_with_filter_panel)

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
        invoiceTypeCheckboxes.forEach { it.value.setOnCheckedChangeListener { _, _ -> onInvoiceTypeCheckChanged(it.value) } }
        paidCheckboxes.forEach { it.setOnCheckedChangeListener { _, _ -> onPaidCheckedChanged(it) } }
        overdueCheckboxes.forEach { it.setOnCheckedChangeListener { _, _ -> onOverdueCheckedChanged(it) } }
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
        resetInvoiceTypeCheckBoxes()
        resetPaidCheckBoxes()
        resetOverdueCheckBoxes()
        resetContactsCheckBoxes()
        resetVenuesCheckBoxes()
        updateUI()
    }

    private fun onFilterDoneButtonPressed() {
        hideFilterPanel()
    }

    private fun onInvoiceTypeCheckChanged(checkBox: CheckBox) {
        val invoiceType = invoiceTypeForCheckBox(checkBox)
        filter = filter.copy(selectedInvoiceType = filter.selectedInvoiceType.toggle(invoiceType))
        delegate?.onFilterChanged(view = this, newFilter = filter)
        updateUI()
    }

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

    private fun onPaidCheckedChanged(checkBox: CheckBox) {
        if (checkBox.isChecked)
            selectedPaidCheckBox.add(checkBox.tag.toString().toBoolean())
        else
            selectedPaidCheckBox.remove(checkBox.tag.toString().toBoolean())

        filter = filter.copy(selectedPaid = selectedPaidCheckBox)
        delegate?.onFilterChanged(view = this, newFilter = filter)
        updateUI()
    }

    private fun onOverdueCheckedChanged(checkBox: CheckBox) {
        if (checkBox.isChecked)
            selectedOverdueCheckBox.add(checkBox.tag.toString().toBoolean())
        else
            selectedOverdueCheckBox.remove(checkBox.tag.toString().toBoolean())


        filter = filter.copy(selectedOverdue = selectedOverdueCheckBox)
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

        clearSearchButton.isVisible = !searchInput.text.isNullOrEmpty()
        clearFilterButton.isVisible = filter.selectedInvoiceType.isNotEmpty() || filter.selectedOverdue.isNotEmpty() || filter.selectedPaid.isNotEmpty() || filter.selectedContacts.isNotEmpty() || filter.selectedVenue.isNotEmpty()

        invoiceDateButtonTextView.text = period.start.toMediumStyleString()
        invoiceDueDateButtonTextView.text = period.endInclusive.toMediumStyleString()


    }

    fun populateVenues(venues: List<VenueBody?>) {
        Handler().postDelayed({

            if (venues.isNotEmpty()) {
                venueTitleTextView.visibility = View.GONE
                venuesLayout.removeAllViews()
                venuesCheckboxes.clear()
            }

            filter.relevantVenues = venues.map { it?.code }


            for (i in venues.indices) {
                if (venues[i]?.code != null) {
                    venueTitleTextView.visibility = View.VISIBLE

                    val checkBox = CheckBox(context)
                    checkBox.id = id
                    checkBox.text = StringBuilder().append(venues[i]?.name).append(" ( ").append(venues[i]?.city).append(" )")
                    checkBox.tag = venues[i]?.code

                    venuesCheckboxes.add(checkBox)
                    venuesLayout.addView(checkBox)

                    checkBox.setOnCheckedChangeListener { _, _ -> onVenueCheckChanged(checkBox) }

                }
            }
        }, 1000)
    }

    fun populateContacts(contacts: List<Contact?>) {
        Handler().postDelayed({
            filter.relevantContacts = contacts.map { it?.id }
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
                    checkBox.tag = contacts[i]?.id

                    contactsCheckboxes.add(checkBox)
                    contactsLayout.addView(checkBox)

                    checkBox.setOnCheckedChangeListener { _, _ -> onContactCheckChanged(checkBox) }
                }

            }
        }, 1000)
    }

    private fun toggleFilterPanel() {
        isFilterPanelOpen = !isFilterPanelOpen
    }

    private fun resetInvoiceTypeCheckBoxes() = invoiceTypeCheckboxes.forEach { it.value.isChecked = false }

    private fun resetPaidCheckBoxes() {
        selectedPaidCheckBox.clear()
        paidCheckboxes.forEach { it.isChecked = false }
    }

    private fun resetOverdueCheckBoxes() {
        selectedOverdueCheckBox.clear()
        overdueCheckboxes.forEach { it.isChecked = false }
    }

    private fun resetContactsCheckBoxes() = contactsCheckboxes.forEach { it.isChecked = false }

    private fun resetVenuesCheckBoxes() = venuesCheckboxes.forEach { it.isChecked = false }

    private fun invoiceTypeForCheckBox(checkBox: CheckBox) = invoiceTypeCheckboxes.filterValues { it == checkBox }.map { it.key }.first()

    private fun showDatePicker(selectedDate: LocalDate, min: LocalDate? = null, max: LocalDate? = null, callback: (date: LocalDate) -> Unit) {
        Dialogs.datePicker(selectedDate, firstDayOfWeek)
                .onDateSet(callback)
                .range(min, max)
                .show(supportFragmentManager, "DatePickerDialog")
    }

/*---------------------------------------- Interfaces ----------------------------------------*/


    interface Delegate {
        fun onBackButtonPressed(view: MyInvoicesSearchBarWithFilterPanel)
        fun onFilterPanelToggled(view: MyInvoicesSearchBarWithFilterPanel, isOpen: Boolean)
        fun onFilterChanged(view: MyInvoicesSearchBarWithFilterPanel, newFilter: MyInvoicesFilter)
    }
}