package com.riwal.rentalapp.project

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Project

interface ProjectsView : MvcView, ObservableLifecycleView {


    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun navigateToEditProjectPage(preparationHandler: ControllerPreparationHandler)
    fun navigateToSummary()

    interface DataSource {
        fun projects(view: ProjectsView): List<Project>
        fun isSelected(view: ProjectsView, project: Project): Boolean
        fun canContinue(view: ProjectsView): Boolean
    }

    interface Delegate {
        fun onBackButtonClicked()
        fun onProjectSelected(project: Project)
        fun onEditProjectSelected(project: Project)
        fun onCreateProjectButtonClicked()
        fun onContinueButtonClicked()
    }

}