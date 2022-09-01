package com.riwal.rentalapp.training.trainingtypes

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.rxjava.disposedBy
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.api.TrainingCategory
import com.riwal.rentalapp.model.api.TrainingResponse
import com.riwal.rentalapp.training.traininglist.TrainingListController
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TrainingTypesController(val view: TrainingTypesView,
                              val activeCountry: Country,
                              private val trainingManager: TrainingManager,
                              private val chatManager: ChatManager,
                              val analytics: RentalAnalytics) : ViewLifecycleObserver, TrainingTypesView.DataSource, TrainingTypesView.Delegate , CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    private var trainings: TrainingResponse? = null
    private var isLoadingTraining = false
    private var hasFailedLoadingTraining = false
    private var messagesSubscription = CompositeDisposable()


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)

    }

    override fun onViewCreate() {
        super.onViewCreate()
        getTrainings()
    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.displayingTrainingTypes()
        observeNumberOfUnreadChatMessages()
    }

    override fun onViewDisappear() {
        super.onViewDisappear()
        messagesSubscription.dispose()
    }


    /*--------------------------- TrainingCategoriesView DataSource ------------------------------*/


    override fun training(view: TrainingTypesView): TrainingResponse? = trainings
    override fun isLoadingTraining(view: TrainingTypesView): Boolean = isLoadingTraining
    override fun hasFailedLoadingTraining(view: TrainingTypesView): Boolean = hasFailedLoadingTraining
    override fun isChatEnabled(view: TrainingTypesView): Boolean = chatManager.isChatEnabled
    override fun isPhoneCallEnable(view: TrainingTypesView): Boolean = activeCountry.isPhoneCallEnable
    override fun rentalDeskContactInfo(view: TrainingTypesView): List<ContactInfo> = activeCountry.rentalDeskContactInfo
    override fun numberOfUnreadMessages(view: TrainingTypesView): Int = chatManager.numberOfUnreadMessages


    /*-------------------------- TrainingCategoriesView Delegate ---------------------------------*/


    override fun onRetryLoadingTrainingSelected() {
        getTrainings()
    }

    override fun onTrainingCategorySelected(view: TrainingTypesView, trainingCategory: TrainingCategory) {
        view.navigateToTrainingCoursesPage { destination ->
            val controller = destination as TrainingListController
            controller.trainingCategory = trainingCategory
        }
    }

    /*------------------------------------- Private methods --------------------------------------*/


    private fun getTrainings() = launch{
        isLoadingTraining = true
        view.notifyDataChanged()
        try {
            trainings = trainingManager.getTrainingRequest(activeCountry)
            hasFailedLoadingTraining = false
        } catch (error: Exception) {
            trainings = null
            hasFailedLoadingTraining = true
        }

        isLoadingTraining = false
        view.notifyDataChanged()
    }

    private fun observeNumberOfUnreadChatMessages() {
        messagesSubscription = CompositeDisposable()
        chatManager
                .observableNumberOfUnreadMessages
                .subscribe { view.notifyDataChanged() }
                .disposedBy(messagesSubscription)
    }


}