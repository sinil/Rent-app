package com.riwal.rentalapp.project

import android.view.MenuItem
import android.view.View
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.android.startActivity
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.common.ui.transition.ModalPushActivityTransition
import com.riwal.rentalapp.project.editproject.EditProjectViewImpl
import com.riwal.rentalapp.summary.SummaryViewImpl
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_project.*

class ProjectsViewImpl : RentalAppNotificationActivity(), ProjectsView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: ProjectsView.DataSource
    override lateinit var delegate: ProjectsView.Delegate

    override val snackbarContainer: View
        get() = contentCoordinator

    private val projects
        get() = dataSource.projects(view = this)

    private val canContinue
        get() = dataSource.canContinue(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_project)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = getString(R.string.page_project)

        recyclerView.dataSource = this
        recyclerView.delegate = this

        addProjectButton.setOnClickListener { delegate.onCreateProjectButtonClicked() }
        continueButton.setOnClickListener { delegate.onContinueButtonClicked() }

        updateUI(animated = false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> delegate.onBackButtonClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        delegate.onBackButtonClicked()
    }


    /*--------------------------------------- ProjectsView ----------------------------------------*/


    override fun navigateBack() = finish()

    override fun navigateToEditProjectPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(EditProjectViewImpl::class, controllerPreparationHandler = preparationHandler, transition = ModalPushActivityTransition)
    }

    override fun navigateToSummary() = startActivity(SummaryViewImpl::class)


    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = projects.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_project
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = ProjectRowViewHolder(itemView)


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val project = projects[indexPath.row]
        viewHolder as ProjectRowViewHolder

        viewHolder.radioButton.setOnCheckedChangeListener(null)
        viewHolder.updateWith(project)
        viewHolder.isChecked = dataSource.isSelected(view = this, project = project)
        viewHolder.radioButton.setOnCheckedChangeListener { _, _ -> delegate.onProjectSelected(project) }
        viewHolder.editButton.setOnClickListener { delegate.onEditProjectSelected(project) }
    }

    override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val project = projects[indexPath.row]
        delegate.onProjectSelected(project)
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    override fun updateUI(animated: Boolean) {
        recyclerView.notifyDataSetChanged()
        emptyProjectView.isVisible = projects.isEmpty()
        recyclerView.isVisible = projects.isNotEmpty()
        continueButton.isEnabled = canContinue
    }

}