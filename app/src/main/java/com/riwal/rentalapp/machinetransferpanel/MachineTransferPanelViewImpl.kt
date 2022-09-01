package com.riwal.rentalapp.machinetransferpanel

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.transition.Slide
import com.jaredrummler.materialspinner.MaterialSpinner
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.animation.transitions
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.onDateSet
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.range
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.Dialogs
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import kotlinx.android.synthetic.main.panel_machine_transfer.view.*
import org.joda.time.LocalDate

class MachineTransferPanelViewImpl @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseView(context, attrs, defStyleAttr), MachineTransferPanelView, MaterialSpinner.OnItemSelectedListener<String> {


    /*---------------------------------------- Properties ----------------------------------------*/

    override lateinit var dataSource: MachineTransferPanelView.DataSource
    override lateinit var delegate: MachineTransferPanelView.Delegate

    private lateinit var customersNameSheetAdapter: ArrayAdapter<String>
    private lateinit var contactNameSheetAdapter: ArrayAdapter<String>

    private var selectedCustomerPosition = 0
    private var selectedContactPosition = 0

    private val canTransferMachine
        get() = dataSource.canTransferMachine(view = this)

    var isOpen = false
        private set

    private val myProject
        get() = dataSource.myProject(view = this)

    private val machines
        get() = dataSource.machineBody(view = this)

    private val transferDate
        get() = dataSource.transferMachineDate(view = this)

    private val country
        get() = dataSource.country(view = this)

    private val validOnRentDateRange
        get() = dataSource.validTransferDate(view = this)



    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        addSubview(R.layout.panel_machine_transfer)


        customersNameSpinnerActionSheet.setOnItemSelectedListener(this)
        customersNameSheetAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, myProject?.customers!!.map { customer -> customer.name })
        customersNameSheetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        customersNameSpinnerActionSheet.setAdapter(customersNameSheetAdapter)

        contactPersonNameSpinner.setOnItemSelectedListener(this)
        contactNameSheetAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, myProject?.customers!![selectedCustomerPosition].contact.map { contact -> contact.name!! })
        contactNameSheetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        contactPersonNameSpinner.setAdapter(contactNameSheetAdapter)

        machineTransferPanelOverlay.setOnClickListener { navigateBack() }
        transferMachineButton.setOnClickListener { delegate.onMachineTransferSelected() }

        selectTransferDateCardView.setOnClickListener {
            showDatePicker { date -> delegate.onTransferDatePicked(date) }
        }

        delegate.onSelectedCustomer(myProject!!.customers[selectedCustomerPosition])
        delegate.onSelectedContact(myProject!!.customers[selectedCustomerPosition].contact[selectedContactPosition])

        updateUI(animated = true)

    }


    override fun updateUI(animated: Boolean) {

        if (animated) {

            val transitionSet = ParallelAutoTransition()
            val bottomSheetVisibilityWillChange = bottomSheet.isVisible != isOpen
            if (bottomSheetVisibilityWillChange) {
                transitionSet.transitions.forEach {
                    it?.excludeTarget(bottomSheet, true)
                }
                transitionSet.addTransition(Slide().addTarget(bottomSheet))
            }

            activity.beginDelayedTransition(transitionSet)
        }


        transferMachineDescriptionTitle.text = getString(R.string.transfer_machine_bottom_sheet_discription, machines?.rentalType)


        customersNameSheetAdapter.clear()
        customersNameSheetAdapter.addAll(myProject?.customers!!.map { customer -> customer.name })
        customersNameSheetAdapter.notifyDataSetChanged()
        customersNameSpinnerActionSheet.selectedIndex = selectedCustomerPosition

        contactNameSheetAdapter.clear()
        contactNameSheetAdapter.addAll(myProject?.customers!![selectedCustomerPosition].contact.map { contact -> contact.name })
        contactNameSheetAdapter.notifyDataSetChanged()
        try {
            contactPersonNameSpinner.selectedIndex = selectedContactPosition
        } catch (e: Exception) {
        }

        val offRentDateString = transferDate?.toMediumStyleString()
        exactTransferDateButtonTextView.text = offRentDateString ?: getString(R.string.rental_panel_date_button_placeholder)


        machineTransferPanelOverlay.isVisible = isOpen
        bottomSheet.isVisible = isOpen
//        transferMachineButton.isEnabled = canTransferMachine

    }


    /*----------------------------------- MachineTransferPanelView -----------------------------------*/


    override fun navigateBack() {
        isOpen = false
        updateUI()
    }

    override fun notifyCannotTransferInWeekend() {

        AlertDialog.Builder(activity)
                .setMessage(R.string.notification_for_delivery_is_not_available_in_weekend)
                .setPositiveButton(R.string.button_ok) { _, _ ->
                    showDatePicker { date -> delegate.onTransferDatePicked(date) }
                }
                .show()
    }


    /*-------------------------------- Spinner Adapter methods -----------------------------------*/


    override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String?) {

        when (view) {
            customersNameSpinnerActionSheet -> {
                selectedCustomerPosition = position
                delegate.onSelectedCustomer(position)
            }
            contactPersonNameSpinner -> {
                selectedContactPosition = position
                delegate.onSelectedContact(position)
            }
        }

        updateUI()
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    fun show(parentView: ViewGroup, preparationHandler: ControllerPreparationHandler) {
        parentView.addView(this, controllerPreparationHandler = preparationHandler)
        isOpen = true
        updateUI()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun showDatePicker(callback: (date: LocalDate) -> Unit) {
        Dialogs.datePicker(transferDate!!, country.firstDayOfWeek)
                .onDateSet(callback)
                .range(validOnRentDateRange)
                .show(supportFragmentManager, "DatePickerDialog")
    }


}