package com.riwal.rentalapp.main

import android.net.NetworkInfo.DetailedState
import android.net.NetworkInfo.DetailedState.CONNECTED
import com.riwal.rentalapp.common.BetterBundle
import com.riwal.rentalapp.common.extensions.rxjava.plusAssign
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.main.MainView.Tab
import com.riwal.rentalapp.main.MainView.Tab.SEARCH
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.AppVersionManager.AppVersionStatus
import com.riwal.rentalapp.model.AppVersionManager.AppVersionStatus.MANDATORY_UPDATE_REQUIRED
import com.riwal.rentalapp.model.AppVersionManager.AppVersionStatus.UPDATE_AVAILABLE
import com.riwal.rentalapp.model.Notification.*
import com.riwal.rentalapp.rentaldetail.RentalDetailController
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.HttpException

class MainController(
        val view: MainView,
        var sessionManager: SessionManager,
        var countryManager: CountryManager,
        val orderManager: OrderManager,
        val chatManager: ChatManager,
        val appVersionManager: AppVersionManager
) : ViewLifecycleObserver, MainView.DataSource, MainView.Delegate, AppVersionManager.Delegate, CountryManager.Observer, CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    var rentalFromNotification: Rental? = null

    private var subscriptions = CompositeDisposable()
    private var activeTab = SEARCH
    private var isDisconnectedFromInternet = false

    private var activeCountry = countryManager.activeCountry!!


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this
        observe(view)
    }

    override fun onViewCreate() {
        super.onViewCreate()

        appVersionManager.delegate = this
        appVersionManager.verify()

        launch { getCustomers() }

        countryManager.addObserver(this)


        if (rentalFromNotification != null) {
            view.navigateToRentalDetailsPage { controller ->
                controller as RentalDetailController
                controller.rental = rentalFromNotification!!
                controller.isViewPresentedModally = true
            }
        }
    }

    override fun onViewAppear() {
        super.onViewAppear()
        view.notifyDataChanged()

        subscriptions = CompositeDisposable()
        subscriptions += chatManager.observableNumberOfUnreadMessages.subscribe {
            view.notifyDataChanged()
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onViewDisappear() {
        super.onViewDisappear()
        subscriptions.dispose()

        EventBus.getDefault().unregister(this)
    }

    override fun onViewSave(state: BetterBundle) {
        super.onViewSave(state)
        state["activeTab"] = activeTab
    }

    override fun onViewRestore(savedState: BetterBundle) {
        super.onViewRestore(savedState)
        activeTab = savedState["activeTab"]!!
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        countryManager.removeObserver(this)
    }


    /*------------------------------------ MainView DataSource -----------------------------------*/


    override fun activeTab(view: MainView) = activeTab
    override fun totalNumberOfOrderedMachines(view: MainView) = orderManager.currentOrder.totalNumberOfOrderedMachines
    override fun isDisconnectedFromInternet(view: MainView) = isDisconnectedFromInternet
    override fun isAccess4UAvailable(view: MainView) = activeCountry.isAccess4UAvailable
    override fun isChatEnabled(view: MainView) = chatManager.isChatEnabled
    override fun numberOfUnreadMessages(view: MainView) = chatManager.numberOfUnreadMessages
    override fun isPhoneCallEnable(view: MainView): Boolean = activeCountry.isPhoneCallEnable
    override fun rentalDeskContactInfo(view: MainView): List<ContactInfo> = activeCountry.rentalDeskContactInfo


    /*------------------------------------- MainView Delegate ------------------------------------*/


    override fun onTabSelected(tab: Tab) {
        activeTab = tab
        view.notifyDataChanged()
    }


    /*-------------------------------- AppVersionManager Delegate --------------------------------*/


    override fun onAppVersionStatusUpdated(appVersionStatus: AppVersionStatus, updateAppURL: String) {
        if (appVersionStatus == MANDATORY_UPDATE_REQUIRED) {
            if (updateAppURL.isNotEmpty()) view.notifyMandatoryAppUpdate(updateAppURL) else view.notifyMandatoryAppUpdate()
        } else if (appVersionStatus == UPDATE_AVAILABLE) {
            view.notifyAppUpdateAvailable()
            appVersionManager.updateLatestDisplayedAppVersionCode()
        }
    }

    override fun isNewFeatureAvailable(informationMessage: String) {
        view.notifyNewFeatureAvailable(informationMessage)
        appVersionManager.updateFeatureDialogHasBeenShowed()
    }

    override fun onAppVersionStatusUpdateFailed(error: Exception) {
        error.printStackTrace()
    }


    /*--------------------------------- CountryManager Observer ----------------------------------*/


    override fun onCountryChanged(countryManager: CountryManager, country: Country) {
        activeCountry = country
        view.notifyDataChanged()
    }


    /*------------------------------------------ Events ------------------------------------------*/


    @Subscribe
    fun onNotificationReceived(notification: Notification) {
        if (notification in listOf(MACHINE_ADDED, MACHINE_CHANGED, MACHINE_REMOVED)) {
            view.notifyDataChanged()
        }
    }

    @Subscribe(sticky = true)
    fun onInternetConnectionChanged(status: DetailedState) {
        isDisconnectedFromInternet = status != CONNECTED
        view.notifyDataChanged()
    }


    /*----------------------------------- private methods  ---------------------------------------*/


    private suspend fun getCustomers() {

        if (sessionManager.isLoggedIn)
            try {
                sessionManager.getCustomers()
            } catch (error: HttpException) {
                if (error.code() == 403)
                    sessionManager.logout()
            } catch (error: Exception) {
                // ignore
            }
    }


}
