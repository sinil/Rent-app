package com.riwal.rentalapp.training.traininglist

import android.os.Handler
import android.view.MenuItem
import android.view.View
import com.codertainment.materialintro.shape.Focus
import com.codertainment.materialintro.shape.ShapeType
import com.codertainment.materialintro.utils.materialIntro
import com.riwal.rentalapp.BuildConfig
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.activity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.training.trainingdetail.TrainingDetailViewImpl
import kotlinx.android.synthetic.main.row_training.view.*
import kotlinx.android.synthetic.main.view_training.*
import kotlinx.android.synthetic.main.view_training.chatButton
import kotlinx.android.synthetic.main.view_training.descriptionTextView
import kotlinx.android.synthetic.main.view_training.phoneCallButton
import kotlinx.android.synthetic.main.view_training.toolbar

class TrainingListViewImpl : RentalAppNotificationActivity(), TrainingListView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: TrainingListView.DataSource
    override lateinit var delegate: TrainingListView.Delegate

    private val trainingType
        get() = dataSource.trainingType(view = this)

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
        setContentView(R.layout.view_training)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = trainingType.Name

        trainingRecyclerView.dataSource = this
        trainingRecyclerView.delegate = this

        trainingCategoryImageView.loadImage(BuildConfig.MY_RIWAL_URL + trainingType.ImageUrl, placeholder = R.drawable.img_machines_contour)

        updateUI(false)

    }


    private fun showMaterialIntro() {
        tutorialLayer.visibility = View.VISIBLE
        if (trainingRecyclerView.getChildAt(1) != null)
            materialIntro(true) {
                focusType = Focus.NORMAL
                infoText = getString(R.string.tutorial_message_training_list)
                okText = getString(R.string.next_button_title)
                skipText = activity.getString(R.string.skip_tutorial_label)
                shapeType = ShapeType.RECTANGLE
                targetView = trainingRecyclerView.getChildAt(1).detailsButton
                layerView = this@TrainingListViewImpl.tutorialLayer
                viewId = "trainingRecyclerView"
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

        descriptionTextView.text = trainingType.Description
        trainingRecyclerView.notifyDataSetChanged()

        Handler().postDelayed({
            if (!isTutorialDisplayed)
                showMaterialIntro()

        }, 800)

    }

    /*----------------------------------------- Actions ------------------------------------------*/


    /*---------------------------- TrainingCategoriesView methods --------------------------------*/

    override fun navigateToTrainingDetailPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(TrainingDetailViewImpl::class, controllerPreparationHandler = preparationHandler)
    }

    override fun navigateBack() = onBackPressed()

    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = trainingType.Courses.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_training
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = TrainingListRowViewHolder(itemView)


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val training = trainingType.Courses[indexPath.row]
        viewHolder as TrainingListRowViewHolder
        viewHolder.updateWith(training, delegate)
    }


}