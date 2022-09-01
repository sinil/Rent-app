package com.riwal.rentalapp.training.trainingtypes

import android.os.Handler
import android.view.MenuItem
import android.view.View
import com.codertainment.materialintro.shape.Focus
import com.codertainment.materialintro.shape.ShapeType
import com.codertainment.materialintro.utils.materialIntro
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.activity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.model.api.TrainingResponse
import com.riwal.rentalapp.training.traininglist.TrainingListViewImpl
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.view_training_types.*

class TrainingTypesViewImpl : RentalAppNotificationActivity(), TrainingTypesView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: TrainingTypesView.DataSource
    override lateinit var delegate: TrainingTypesView.Delegate

    private val trainings: TrainingResponse?
        get() = dataSource.training(view = this)


    private val isLoadingTraining
        get() = dataSource.isLoadingTraining(view = this)

    private val hasFailedLoadingTraining
        get() = dataSource.hasFailedLoadingTraining(view = this)

    private val isChatEnabled
        get() = dataSource.isChatEnabled(view = this)

    private val isPhoneCallEnable
        get() = dataSource.isPhoneCallEnable(view = this)

    private val rentalContactNumber
        get() = dataSource.rentalDeskContactInfo(view = this)[0].phoneNumber

    private val numberOfUnreadMessages
        get() = dataSource.numberOfUnreadMessages(view = this)

    private var isTutorialDisplayed = false


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_training_types)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = getString(R.string.training_types)

        retryLoadingTrainingContainer.setOnClickListener { delegate.onRetryLoadingTrainingSelected() }

        trainingCategoriesRecyclerView.dataSource = this
        trainingCategoriesRecyclerView.delegate = this

        updateUI(false)

    }


    private fun showMaterialIntro() {
        tutorialLayer.visibility = View.VISIBLE
        if (trainingCategoriesRecyclerView.getChildAt(1) != null)
            materialIntro(true) {
                focusType = Focus.NORMAL
                infoText = getString(R.string.tutorial_message_training_category)
                okText = getString(R.string.next_button_title)
                skipText = activity.getString(R.string.skip_tutorial_label)
                shapeType = ShapeType.RECTANGLE
                layerView = this@TrainingTypesViewImpl.tutorialLayer
                targetView = trainingCategoriesRecyclerView.getChildAt(1)
                viewId = "trainingCategoriesRecyclerView"
                isTutorialDisplayed = true
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> navigateBack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun updateUI(animated: Boolean) {

        chatButton.isVisible = isChatEnabled
        chatButton.numberOfUnreadMessages = numberOfUnreadMessages

        phoneCallButton.isVisible = isPhoneCallEnable
        phoneCallButton.phoneNumber = rentalContactNumber

        retryLoadingTrainingContainer.isVisible = !isLoadingTraining && hasFailedLoadingTraining
        emptyTrainingView.isVisible = trainings == null && !isLoadingTraining && !hasFailedLoadingTraining
        activityIndicator.isVisible = isLoadingTraining

        if (trainings?.ServiceInfo != null) descriptionTextView.text = trainings?.ServiceInfo else descriptionTextView.visibility = View.GONE
        trainingCategoriesRecyclerView.notifyDataSetChanged()

        Handler().postDelayed({
            if (!isLoadingTraining && !hasFailedLoadingTraining && trainings != null && !isTutorialDisplayed)
                showMaterialIntro()

        }, 800)

    }
    /*----------------------------------------- Actions ------------------------------------------*/


    /*---------------------------- TrainingCategoriesView methods --------------------------------*/


    override fun navigateToTrainingCoursesPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(TrainingListViewImpl::class, controllerPreparationHandler = preparationHandler)
    }

    override fun navigateBack() = onBackPressed()


    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = if (trainings?.Categories != null) trainings?.Categories!!.size else 0
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_training_category
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = TrainingTypeRowViewHolder(itemView)


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val trainingCategory = trainings!!.Categories[indexPath.row]
        viewHolder as TrainingTypeRowViewHolder
        viewHolder.updateWith(trainingCategory)
    }

    override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val trainingCategory = trainings!!.Categories[indexPath.row]
        delegate.onTrainingCategorySelected(view = this, trainingCategory = trainingCategory)
    }

}