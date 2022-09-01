package com.riwal.rentalapp.requestchangesform

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.android.items
import com.riwal.rentalapp.common.extensions.datetime.today
import com.riwal.rentalapp.common.extensions.datetime.withDate
import com.riwal.rentalapp.common.extensions.datetime.withTime
import com.riwal.rentalapp.common.extensions.materialdatetimepicker.*
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.Dialogs
import com.riwal.rentalapp.common.ui.EditTextView
import com.riwal.rentalapp.placepicker.PlacePickerViewImpl
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.common.ui.transition.ModalPushActivityTransition
import com.riwal.rentalapp.model.AccessoriesOnRent
import com.riwal.rentalapp.model.Notification
import com.riwal.rentalapp.model.Project
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.requestchangesform.ChangeRequestFormView.DataSource
import com.riwal.rentalapp.requestchangesform.ChangeRequestFormView.Delegate
import kotlinx.android.synthetic.main.dialog_comment.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_change_rental.*
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime

class ChangeRequestFormViewImpl : RentalAppNotificationActivity(), ChangeRequestFormView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    private var doneMenuItem: MenuItem? = null

    private val rental
        get() = dataSource.rental(view = this)

    private val accessoriesOnRent
        get() = dataSource.accessories(view = this)

    private val canChangeOnRentDateTime
        get() = dataSource.canChangeOnRentDateTime(view = this)

    private val canChangeOffRentDateTime
        get() = dataSource.canChangeOffRentDateTime(view = this)

    private val canSubmit
        get() = dataSource.canSubmit(view = this)

    private val firstDayOfWeek
        get() = dataSource.firstDayOfWeek(view = this)

    private val onRentDateTime
        get() = if (rental != null) rental?.onRentDateTime else accessoriesOnRent?.onRentDateTime

    private val offRentDateTime
        get() = if (rental != null) rental?.offRentDateTime else accessoriesOnRent?.offRentDateTime

    private val purchaseOrder
        get() = if (rental != null) rental?.purchaseOrder else accessoriesOnRent?.purchaseOrder

    private val project
        get() = if (rental != null) rental?.project else accessoriesOnRent?.project

    /*----------------------------------------- Lifecycle ----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_change_rental)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close, tint = color(R.color.app_bar_text))
        title = "${rental?.rentalType} - ${getString(R.string.request_changes)}"

        updateUI(animated = false)

        projectNameEditText.onTextChangedListener = { onEditTextChanged(projectNameEditText) }
        projectAddressEditText.onTextChangedListener = { onEditTextChanged(projectAddressEditText) }
        projectContactNameEditText.onTextChangedListener = { onEditTextChanged(projectContactNameEditText) }
        projectContactPhoneNumberEditText.onTextChangedListener = { onEditTextChanged(projectContactPhoneNumberEditText) }
        purchaseOrderEditText.onTextChangedListener = { onEditTextChanged(purchaseOrderEditText) }
        onRentDateEditText.setOnClickListener { onOnRentDateFieldPressed() }
        deliveryTimeEditText.setOnClickListener { onDeliveryTimeFieldPressed() }
        offRentDateEditText.setOnClickListener { onOffRentDateFieldPressed() }
        offRentTimeEditText.setOnClickListener { onOffRentTimeFieldPressed() }
        projectAddressEditText.setOnClickListener { delegate.onPickPlaceSelected(view = this) }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_request_changes, menu)
        doneMenuItem = menu.findItem(R.id.doneItem)

        menu.items.forEach { it.icon?.setTintList(R.color.toolbar_button) }

        updateUI(animated = false)
        return true
    }

    override fun shouldDeferNotification(notification: Notification) = true


    /*----------------------------------------- Actions ------------------------------------------*/


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            delegate.onBackPressed(view = this); true
        }
        R.id.doneItem -> {
            delegate.onSubmitChangesSelected(view = this); true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun onEditTextChanged(editText: EditTextView) {

        val newValue = editText.text ?: ""

        when (editText) {
            projectNameEditText -> updateProject { it.copy(name = newValue) }
            projectAddressEditText -> updateProject { it.copy(address = newValue) }
            projectContactNameEditText -> updateProject { it.copy(contactName = newValue) }
            projectContactPhoneNumberEditText -> updateProject { it.copy(contactPhoneNumber = newValue) }
            purchaseOrderEditText -> if(rental != null) updateRental { it.copy(purchaseOrder = newValue) } else updateAccessories { it.copy(purchaseOrder = newValue) }
        }

    }

    private fun onOnRentDateFieldPressed() {
        // The fact that "today" is the minimum is duplicate logic with the RentalPanelController and should be put in a model class
        pickDateComponentForDateTime(onRentDateTime!!, minimum = today, pickerTitle = getString(R.string.change_request_form_on_rent_date_field_label)) { newDateTime ->
            if (rental != null) updateRental { it.copy(onRentDateTime = newDateTime) } else updateAccessories { it.copy(onRentDateTime = newDateTime) }
        }
    }

    private fun onDeliveryTimeFieldPressed() {
        pickTimeComponentForDateTime(onRentDateTime!!, pickerTitle = getString(R.string.change_request_form_on_rent_time_field_label)) { newDateTime ->
            if (rental != null) updateRental { it.copy(onRentDateTime = newDateTime) } else updateAccessories { it.copy(onRentDateTime = newDateTime) }
        }
    }

    private fun onOffRentDateFieldPressed() {
        pickDateComponentForDateTime(onRentDateTime!!, minimum = today, pickerTitle = getString(R.string.change_request_form_off_rent_date_field_label)) { newDateTime ->
            if (rental != null) updateRental { it.copy(onRentDateTime = newDateTime) } else updateAccessories { it.copy(onRentDateTime = newDateTime) }
        }

    }

    private fun onOffRentTimeFieldPressed() {
        pickTimeComponentForDateTime(offRentDateTime!!, pickerTitle = getString(R.string.change_request_form_off_rent_time_field_label)) { newDateTime ->
            if (rental != null) updateRental { it.copy(offRentDateTime = newDateTime) } else updateAccessories { it.copy(offRentDateTime = newDateTime) }
        }
    }

    override fun onBackPressed() {
        delegate.onBackPressed(view = this)
    }


    /*----------------------------------- Request Changes View -----------------------------------*/


    override fun navigateBack() = finish(transition = ModalPopActivityTransition)

    override fun navigateToPlacePickerPage(controllerPreparationHandler: ControllerPreparationHandler) {
        startActivity(PlacePickerViewImpl::class, controllerPreparationHandler = controllerPreparationHandler, transition = ModalPushActivityTransition)
    }

    override fun askForComments(completionHandler: (comments: String) -> Unit) = onUiThread {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_confirm_request_change_title))
                .setMessage(getString(R.string.dialog_confirm_request_change_message))
                .setView(R.layout.dialog_comment)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(getString(R.string.confirm)) { iDialog, _ ->
                    val comments = (iDialog as AlertDialog).commentsEditText.text.toString()
                    completionHandler(comments)
                }
                .show()
    }



    /*-------------------------------------- Private Methods -------------------------------------*/


    override fun updateUI(animated: Boolean) {

        val project = project

        doneMenuItem?.isEnabled = canSubmit
        projectNameEditText.text = project!!.name
        projectAddressEditText.text = project.address
        projectContactNameEditText.text = project.contactName
        projectContactPhoneNumberEditText.text = project.contactPhoneNumber
        onRentSection.isVisible = canChangeOnRentDateTime
        onRentDateEditText.text = onRentDateTime!!.toLocalDate().toMediumStyleString()
        deliveryTimeEditText.text = onRentDateTime!!.toLocalTime().toShortStyleString()
        offRentSection.isVisible = canChangeOffRentDateTime
        offRentDateEditText.text = offRentDateTime?.toLocalDate()?.toMediumStyleString()
        offRentTimeEditText.text = offRentDateTime?.toLocalTime()?.toShortStyleString()
        purchaseOrderEditText.text = purchaseOrder

    }

    private fun updateRental(transform: (Rental) -> Rental) {
        delegate.onRentalChanged(view = this, changedRental = transform(rental!!))
    }

    private fun updateAccessories(transform: (AccessoriesOnRent) -> AccessoriesOnRent) {
        delegate.onAccessoriesChanged(view = this, changedAccessories = transform(accessoriesOnRent!!))
    }

    private fun updateProject(transform: (Project) -> Project) {
        if (rental != null) updateRental { it.copy(project = transform(rental!!.project)) } else updateAccessories { it.copy(project = transform(accessoriesOnRent!!.project)) }
    }

    private fun pickDateComponentForDateTime(dateTime: LocalDateTime, minimum: LocalDate? = null, maximum: LocalDate? = null, pickerTitle: String, callback: (newDateTime: LocalDateTime) -> Unit) {

        Dialogs.datePicker(dateTime.toLocalDate(), firstDayOfWeek)
                .title(pickerTitle)
                .range(minimum, maximum)
                .onDateSet { date ->
                    val newDateTime = dateTime.withDate(date)
                    callback(newDateTime)
                }
                .show(supportFragmentManager, "DatePickerDialog")
    }

    private fun pickTimeComponentForDateTime(dateTime: LocalDateTime, minimum: LocalTime? = null, maximum: LocalTime? = null, pickerTitle: String, callback: (newDateTime: LocalDateTime) -> Unit) {
        Dialogs.timePicker(dateTime.toLocalTime())
                .title(pickerTitle)
                .range(minimum, maximum)
                .interval(hours = 1, minutes = 15)
                .onTimeSet { time ->
                    val newDateTime = dateTime.withTime(time)
                    callback(newDateTime)
                }
                .show(supportFragmentManager, "TimePickerDialog")
    }

}
