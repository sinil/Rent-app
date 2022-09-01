package com.riwal.rentalapp.project.editproject

import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.placepicker.PlacePickerViewImpl
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.common.ui.transition.ModalPushActivityTransition
import com.riwal.rentalapp.project.editproject.EditProjectStyle.CREATE
import com.riwal.rentalapp.project.editproject.EditProjectStyle.EDIT
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_edit_project.*

class EditProjectViewImpl : RentalAppNotificationActivity(), EditProjectView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: EditProjectView.DataSource
    override lateinit var delegate: EditProjectView.Delegate

    private val project
        get() = dataSource.project(view = this)

    private val style
        get() = dataSource.style(view = this)

    private val isProjectContactSameAsOrderContact
        get() = dataSource.isProjectContactSameAsOrderContact(view = this)

    private val isPhoneNumberValid
        get() = dataSource.isPhoneNumberValid(view = this)

    private val canFinish
        get() = dataSource.canFinish(view = this)

    private var finishMenuItem: MenuItem? = null
    private var deleteMenuItem: MenuItem? = null


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_edit_project)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_close, tint = color(R.color.app_bar_text))

        title = if (style == CREATE) getString(R.string.page_create_project) else getString(R.string.page_edit_project)

        projectNameEditText.onTextChangedListener = { delegate.onProjectNameChanged(view = this, newValue = it) }
        projectNameEditText.onEditorActionListener = { actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                clearFocusAndHideKeyboard()
                delegate.onPickPlaceSelected(view = this)
            }
        }

        contactSameAsOrderContactCheckBox.isChecked = isProjectContactSameAsOrderContact // Keep this here to prevent unnecessary triggering the listener
        contactSameAsOrderContactCheckBox.setOnCheckedChangeListener { _, isChecked -> delegate.onContactSameAsOrderContactChanged(view = this, contactSameAsOrderContact = isChecked) }
        projectContactNameEditText.onTextChangedListener = { delegate.onContactNameChanged(view = this, newValue = it) }
        projectContactPhoneNumberEditText.onTextChangedListener = { delegate.onContactPhoneNumberChanged(view = this, newValue = it) }
        projectAddressEditText.setOnClickListener { delegate.onPickPlaceSelected(view = this) }

        updateUI(animated = false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_create_project, menu)

        finishMenuItem = menu.findItem(R.id.doneItem)
        deleteMenuItem = menu.findItem(R.id.deleteItem)

        menu.items.forEach { it.icon?.setTintList(R.color.toolbar_button) }
        updateUI(animated = false)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.doneItem -> {
            delegate.onFinishEditingSelected(view = this); true
        }
        R.id.deleteItem -> {
            delegate.onDeleteProjectSelected(view = this); true
        }
        android.R.id.home -> {
            delegate.onCancelSelected(view = this); true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        delegate.onCancelSelected(view = this)
    }

    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition()
        }

        projectNameEditText.text = project.name
        projectAddressEditText.text = project.address
        projectContactNameEditText.text = project.contactName
        projectContactPhoneNumberEditText.text = project.contactPhoneNumber
        projectContactPhoneNumberEditText.error = if (projectContactPhoneNumberEditText.text.isNullOrBlank() || isPhoneNumberValid) null else getString(R.string.error_invalid_phone_number)
        contactSameAsOrderContactCheckBox.isChecked = isProjectContactSameAsOrderContact
        finishMenuItem?.isEnabled = canFinish
        deleteMenuItem?.isVisible = style == EDIT

    }


    /*------------------------------------ EditProjectView ---------------------------------------*/


    override fun notifyDataChanged() {
        super.notifyDataChanged()
        invalidateOptionsMenu()
    }

    override fun navigateBack() = finish(transition = ModalPopActivityTransition)

    override fun navigateToPlacePickerPage(controllerPreparationHandler: ControllerPreparationHandler) {
        startActivity(PlacePickerViewImpl::class, controllerPreparationHandler = controllerPreparationHandler, transition = ModalPushActivityTransition)
    }

    override fun confirmProjectDeletion(confirmHandler: (Boolean) -> Unit) {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_delete_project_title))
                .setMessage(getString(R.string.dialog_delete_project_message))
                .setPositiveButton(R.string.delete) { _, _ -> confirmHandler(true) }
                .setNegativeButton(R.string.cancel) { _, _ -> confirmHandler(false) }
                .setOnCancelListener { confirmHandler(false) }
                .show()
    }

}