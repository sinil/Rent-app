package com.riwal.rentalapp.training.trainingdetail

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.rxjava.disposedBy
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.model.ChatManager
import com.riwal.rentalapp.model.ContactInfo
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.api.TrainingCourse
import com.riwal.rentalapp.training.requesttrainingform.RequestTrainingFormController
import io.reactivex.disposables.CompositeDisposable

class TrainingDetailController(val view: TrainingDetailView,
                               val activeCountry: Country,
                               val chatManager: ChatManager,
                               val analytics: RentalAnalytics) : ViewLifecycleObserver, TrainingDetailView.DataSource, TrainingDetailView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    lateinit var trainingCourse: TrainingCourse
    private var messagesSubscription = CompositeDisposable()


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.displayingTrainingDetails(trainingCourse.Name)
        observeNumberOfUnreadChatMessages()
    }

    override fun onViewDisappear() {
        super.onViewDisappear()
        messagesSubscription.dispose()
    }


    /*--------------------------- TrainingCategoriesView DataSource ------------------------------*/


    override fun trainingCourse(view: TrainingDetailView): TrainingCourse = trainingCourse
    override fun isChatEnabled(view: TrainingDetailView): Boolean = chatManager.isChatEnabled
    override fun isPhoneCallEnable(view: TrainingDetailView): Boolean = activeCountry.isPhoneCallEnable
    override fun rentalDeskContactInfo(view: TrainingDetailView): List<ContactInfo> = activeCountry.rentalDeskContactInfo
    override fun numberOfUnreadMessages(view: TrainingDetailView): Int = chatManager.numberOfUnreadMessages

    /*-------------------------- TrainingCategoriesView Delegate ---------------------------------*/


    override fun onRequestQuoteSelected() {
        analytics.requestQuoteSelected()
        view.navigateToRequestQuotePage { destination ->
            val controller = destination as RequestTrainingFormController
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