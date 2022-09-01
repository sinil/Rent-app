package com.riwal.rentalapp.project.editproject

import com.riwal.rentalapp.common.extensions.core.isPhoneNumber
import com.riwal.rentalapp.placepicker.PlacePickerController
import com.riwal.rentalapp.model.Contact
import com.riwal.rentalapp.model.Place
import com.riwal.rentalapp.model.Project
import com.riwal.rentalapp.project.editproject.EditProjectStyle.EDIT

class EditProjectController(val view: EditProjectView) : EditProjectView.DataSource, EditProjectView.Delegate, PlacePickerController.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    var delegate: Delegate? = null
    var project = Project()
    var style = EDIT
    var currentProjects: List<Project> = emptyList()
    var orderContact: Contact? = null

    private val isProjectContactSameAsOrderContact
        get() = orderContact?.name == project.contactName &&
                orderContact?.phoneNumber == project.contactPhoneNumber

    private val isPhoneNumberValid
        get() = project.contactPhoneNumber?.isPhoneNumber ?: false

    private val canFinish
        get() = allFieldsFilledOut && allFieldsValid && !projectAlreadyExists

    private val otherProjects
        get() = currentProjects.filter { it.id != project.id }

    private val projectAlreadyExists
        get() = project.copy(id = 0) in otherProjects.map { it.copy(id = 0) }

    private val allFieldsFilledOut: Boolean
        get() {
            val values = with(project) { listOf(name, contactName, contactPhoneNumber, address) }
            return values.none { it.isNullOrBlank() }
        }

    private val allFieldsValid
        get() = isPhoneNumberValid


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this
    }


    /*------------------------------------- View DataSource --------------------------------------*/


    override fun project(view: EditProjectView) = project
    override fun style(view: EditProjectView) = style
    override fun isProjectContactSameAsOrderContact(view: EditProjectView) = isProjectContactSameAsOrderContact
    override fun isPhoneNumberValid(view: EditProjectView) = isPhoneNumberValid
    override fun canFinish(view: EditProjectView) = canFinish


    /*-------------------------------------- View Delegate ---------------------------------------*/


    override fun onProjectNameChanged(view: EditProjectView, newValue: String) {
        project.name = newValue
        view.notifyDataChanged()
    }

    override fun onPickPlaceSelected(view: EditProjectView) {
        view.navigateToPlacePickerPage { controller ->
            controller as PlacePickerController
            controller.delegate = this
        }
    }

    override fun onContactNameChanged(view: EditProjectView, newValue: String) {
        project.contactName = newValue
        view.notifyDataChanged()
    }

    override fun onContactPhoneNumberChanged(view: EditProjectView, newValue: String) {
        project.contactPhoneNumber = newValue
        view.notifyDataChanged()
    }

    override fun onContactSameAsOrderContactChanged(view: EditProjectView, contactSameAsOrderContact: Boolean) {

        if (contactSameAsOrderContact) {
            project.contactName = orderContact?.name
            project.contactPhoneNumber = orderContact?.phoneNumber
        } else {
            project.contactName = null
            project.contactPhoneNumber = null
        }

        view.notifyDataChanged()
    }

    override fun onFinishEditingSelected(view: EditProjectView) {
        delegate?.onEditingFinished(controller = this, project = project)
        view.navigateBack()
    }

    override fun onDeleteProjectSelected(view: EditProjectView) {
        view.confirmProjectDeletion { confirmed ->
            if (confirmed) {
                delegate?.onProjectDeleted(controller = this, project = project)
                view.navigateBack()
            }
        }
    }

    override fun onCancelSelected(view: EditProjectView) {
        view.navigateBack()
    }


    /*------------------------------ PlacePickerController Delegate ------------------------------*/


    override fun onPlacePicked(controller: PlacePickerController, place: Place) {
        project.address = place.address
        project.coordinate = place.coordinate
        view.notifyDataChanged()
    }


    /*---------------------------------------- Interfaces ----------------------------------------*/


    interface Delegate {
        fun onEditingFinished(controller: EditProjectController, project: Project)
        fun onProjectDeleted(controller: EditProjectController, project: Project)
    }

}