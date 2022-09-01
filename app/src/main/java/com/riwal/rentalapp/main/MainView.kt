package com.riwal.rentalapp.main


import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.ContactInfo

interface MainView : MvcView, ObservableLifecycleView {

    enum class Tab {
        SEARCH,
        CART,
        ACCOUNT,
        MORE
    }

    var dataSource: DataSource
    var delegate: Delegate

    fun notifyMandatoryAppUpdate()
    fun notifyMandatoryAppUpdate(updateAppURL: String)
    fun notifyAppUpdateAvailable()
    fun notifyNewFeatureAvailable(infoMessage: String)
    fun navigateToRentalDetailsPage(preparationHandler: ControllerPreparationHandler)

    interface DataSource {
        fun activeTab(view: MainView): Tab
        fun totalNumberOfOrderedMachines(view: MainView): Int
        fun isAccess4UAvailable(view: MainView): Boolean
        fun isChatEnabled(view: MainView): Boolean
        fun numberOfUnreadMessages(view: MainView): Int
        fun isDisconnectedFromInternet(view: MainView): Boolean
        fun isPhoneCallEnable(view: MainView): Boolean
        fun rentalDeskContactInfo(view: MainView): List<ContactInfo>
    }

    interface Delegate {
        fun onTabSelected(tab: Tab)
    }

}
