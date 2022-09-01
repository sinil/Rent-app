package com.riwal.rentalapp.training.traininglist

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.model.ChatManager
import com.riwal.rentalapp.common.extensions.rxjava.disposedBy
import com.riwal.rentalapp.model.ContactInfo
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.api.TrainingCategory
import com.riwal.rentalapp.model.api.TrainingCourse
import com.riwal.rentalapp.training.trainingdetail.TrainingDetailController
import io.reactivex.disposables.CompositeDisposable

class TrainingListController(val view: TrainingListView,
                             val activeCountry: Country,
                             val chatManager: ChatManager,
                             val analytics: RentalAnalytics) : ViewLifecycleObserver, TrainingListView.DataSource, TrainingListView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    lateinit var trainingCategory: TrainingCategory
    private var messagesSubscription = CompositeDisposable()


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.displayingTrainingSubTypes(trainingCategory.Name)
        observeNumberOfUnreadChatMessages()
    }

    override fun onViewDisappear() {
        super.onViewDisappear()
        messagesSubscription.dispose()
    }


    /*--------------------------- TrainingCategoriesView DataSource ------------------------------*/


    override fun trainingType(view: TrainingListView): TrainingCategory = trainingCategory
    override fun isChatEnabled(view: TrainingListView): Boolean = chatManager.isChatEnabled
    override fun isPhoneCallEnable(view: TrainingListView): Boolean = activeCountry.isPhoneCallEnable
    override fun rentalDeskContactInfo(view: TrainingListView): List<ContactInfo> = activeCountry.rentalDeskContactInfo
    override fun numberOfUnreadMessages(view: TrainingListView): Int = chatManager.numberOfUnreadMessages


    /*-------------------------- TrainingCategoriesView Delegate ---------------------------------*/


    override fun onTrainingDetailSelected(trainingCourse: TrainingCourse) {
        view.navigateToTrainingDetailPage { destination ->
            val controller = destination as TrainingDetailController
            controller.trainingCourse = trainingCourse
        }
    }

    /*------------------------------------- Private methods --------------------------------------*/


    private fun observeNumberOfUnreadChatMessages() {
        messagesSubscription = CompositeDisposable()
        chatManager
                .observableNumberOfUnreadMessages
                .subscribe { view.notifyDataChanged() }
                .disposedBy(messagesSubscription)
    }


}