package com.riwal.rentalapp.project

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.OrderManager
import com.riwal.rentalapp.model.Project
import com.riwal.rentalapp.model.ProjectsManager
import com.riwal.rentalapp.project.editproject.EditProjectController
import com.riwal.rentalapp.project.editproject.EditProjectStyle
import com.riwal.rentalapp.project.editproject.EditProjectStyle.CREATE
import com.riwal.rentalapp.project.editproject.EditProjectStyle.EDIT

class ProjectsController(val view: ProjectsView, val orderManager: OrderManager, val projectsManager: ProjectsManager, val analytics: RentalAnalytics) : ViewLifecycleObserver, ProjectsView.DataSource, ProjectsView.Delegate, EditProjectController.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    private val order
        get() = orderManager.currentOrder

    private var selectedProject
        get() = order.project
        set(value) {
            orderManager.currentOrder.project = value
            orderManager.save()
            view.notifyDataChanged()
        }

    private val projects
        get() = projectsManager.projects


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewAppear() {
        super.onViewAppear()

        analytics.userLookingAtProjectsPage()

        if (projects.size == 1) {
            selectedProject = projects.first()
            view.notifyDataChanged()
        }

    }


    /*---------------------------------- ProjectsView DataSource ----------------------------------*/


    override fun projects(view: ProjectsView) = projects
    override fun isSelected(view: ProjectsView, project: Project) = selectedProject == project
    override fun canContinue(view: ProjectsView) = selectedProject != null


    /*----------------------------------- ProjectsView Delegate -----------------------------------*/


    override fun onBackButtonClicked() {
        view.navigateBack()
    }

    override fun onProjectSelected(project: Project) {
        selectedProject = project
        view.notifyDataChanged()
    }

    override fun onEditProjectSelected(project: Project) {
        view.navigateToEditProjectPage { destination ->
            val controller = destination as EditProjectController
            prepareEditProject(controller, project = project.copy(), style = EDIT)
        }
    }

    override fun onCreateProjectButtonClicked() {
        view.navigateToEditProjectPage { destination ->
            val controller = destination as EditProjectController
            prepareEditProject(controller, project = Project(), style = CREATE)
        }
    }

    override fun onContinueButtonClicked() {
        view.navigateToSummary()
    }


    /*------------------------------ EditProjectController Delegate ------------------------------*/


    override fun onEditingFinished(controller: EditProjectController, project: Project) {
        projectsManager.put(project)
        selectedProject = project
    }

    override fun onProjectDeleted(controller: EditProjectController, project: Project) {
        projectsManager.remove(project)
        if (projects.count() == 1) {
            selectedProject = projects.first()
        } else if (selectedProject == project) {
            selectedProject = null
        }
        view.notifyDataChanged()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun prepareEditProject(controller: EditProjectController, project: Project, style: EditProjectStyle) {
        controller.project = project
        controller.style = style
        controller.currentProjects = projects
        controller.orderContact = order.contact
        controller.delegate = this
    }

}