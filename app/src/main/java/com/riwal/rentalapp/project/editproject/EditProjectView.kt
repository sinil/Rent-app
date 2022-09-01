package com.riwal.rentalapp.project.editproject

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Place
import com.riwal.rentalapp.model.Project

interface EditProjectView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun navigateToPlacePickerPage(controllerPreparationHandler: ControllerPreparationHandler)
    fun confirmProjectDeletion(confirmHandler: (Boolean) -> Unit)

    interface DataSource {
        fun project(view: EditProjectView): Project
        fun style(view: EditProjectView): EditProjectStyle
        fun isProjectContactSameAsOrderContact(view: EditProjectView): Boolean
        fun isPhoneNumberValid(view: EditProjectView): Boolean
        fun canFinish(view: EditProjectView): Boolean
    }

    interface Delegate {
        fun onProjectNameChanged(view: EditProjectView, newValue: String)
        fun onPickPlaceSelected(view: EditProjectView)
        fun onContactNameChanged(view: EditProjectView, newValue: String)
        fun onContactPhoneNumberChanged(view: EditProjectView, newValue: String)
        fun onContactSameAsOrderContactChanged(view: EditProjectView, contactSameAsOrderContact: Boolean)
        fun onFinishEditingSelected(view: EditProjectView)
        fun onDeleteProjectSelected(view: EditProjectView)
        fun onCancelSelected(view: EditProjectView)

    }
}