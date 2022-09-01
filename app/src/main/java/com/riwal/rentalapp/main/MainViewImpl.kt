package com.riwal.rentalapp.main

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.riwal.rentalapp.BuildConfig
import com.riwal.rentalapp.BuildConfig.FLAVOR_brand
import com.riwal.rentalapp.BuildConfig.FLAVOR_environment
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.widgets.navigationItemAt
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.BadgeView
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.transition.FadeActivityTransition
import com.riwal.rentalapp.main.MainView.DataSource
import com.riwal.rentalapp.main.MainView.Tab
import com.riwal.rentalapp.main.MainView.Tab.*
import com.riwal.rentalapp.main.account.AccountViewImpl
import com.riwal.rentalapp.main.cart.CartViewImpl
import com.riwal.rentalapp.main.more.MoreViewImpl
import com.riwal.rentalapp.main.search.SearchViewImpl
import com.riwal.rentalapp.model.Notification
import com.riwal.rentalapp.model.Notification.REACHABILITY_CHANGED
import com.riwal.rentalapp.rentaldetail.RentalDetailViewImpl
import kotlinx.android.synthetic.main.view_main.*

class MainViewImpl : RentalAppNotificationActivity(), MainView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: MainView.Delegate

    override val snackbarContainer: View
        get() = contentCoordinator

    private lateinit var searchView: SearchViewImpl
    private lateinit var cartView: CartViewImpl
    private lateinit var accountView: AccountViewImpl
    private lateinit var moreView: MoreViewImpl
    private lateinit var activeView: BaseView

    private lateinit var badge: BadgeView

    lateinit var tutorialLayer: View

    private val activeTab
        get() = dataSource.activeTab(view = this)

    private val totalNumberOfOrderedMachines
        get() = dataSource.totalNumberOfOrderedMachines(view = this)

    private val isAccess4UAvailable
        get() = dataSource.isAccess4UAvailable(view = this)

    private val isChatEnabled
        get() = dataSource.isChatEnabled(view = this)

    private val isPhoneCallEnable
        get() = dataSource.isPhoneCallEnable(view = this)

    private val rentalcontactNumber
        get() = dataSource.rentalDeskContactInfo(view = this)[0].phoneNumber

    private val numberOfUnreadMessages
        get() = dataSource.numberOfUnreadMessages(view = this)

    private val isDisconnectedFromInternet
        get() = dataSource.isDisconnectedFromInternet(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_main)

        searchView = SearchViewImpl(this)
        cartView = CartViewImpl(this)
        accountView = AccountViewImpl(this)
        moreView = MoreViewImpl(this)
        activeView = searchView

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val tab = when (item.itemId) {
                R.id.nav_search -> SEARCH
                R.id.nav_cart -> CART
                R.id.nav_account -> ACCOUNT
                R.id.nav_more -> MORE
                else -> SEARCH
            }
            if (activeTab != tab) {
                delegate.onTabSelected(tab)
            }

            true
        }

        badge = BadgeView(this)
        badge.translationX = dp(16f)

        tutorialLayer = findViewById(R.id.tutorialLayer)

        updateUI(animated = false)
    }

    fun toggleTutorialLayerVisibility(visible: Boolean) {
        tutorialLayer.isVisible = visible
    }

    override fun onBackPressed() {
        if (!activeView.onBackPressed()) {
            finish(transition = FadeActivityTransition)
        }
    }

    override fun shouldShowNotification(notification: Notification) = when (notification) {
        REACHABILITY_CHANGED -> false
        else -> true
    }


    /*------------------------------------- Main View methods ------------------------------------*/


    override fun notifyMandatoryAppUpdate(): Unit = onUiThread {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_update_app_title))
                .setMessage(getString(R.string.dialog_update_app_required_message))
                .setPositiveButton(getString(R.string.update)) { _, _ ->  navigateToPlayStore() }
                .setOnDismissListener { notifyMandatoryAppUpdate() }
                .setCancelable(false)
                .show()
    }

    override fun notifyMandatoryAppUpdate(updateAppURL: String): Unit = onUiThread  {
        val message = if (FLAVOR_brand == "riwal") getString(R.string.dialog_install_app_required_message) else getString(R.string.dialog_install_app_required_message_manlift)
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_install_app_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.install_title)) { _, _ -> navigateToPlayStore(updateAppURL) }
                .setOnDismissListener { notifyMandatoryAppUpdate(updateAppURL) }
                .setCancelable(false)
                .show()
    }

    override fun notifyAppUpdateAvailable() = onUiThread {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_update_app_title))
                .setMessage(getString(R.string.dialog_update_app_message))
                .setNegativeButton(getString(R.string.dialog_update_app_cancel_button), null)
                .setPositiveButton(getString(R.string.update)) { _, _ -> navigateToPlayStore() }
                .show()
    }

    override fun notifyNewFeatureAvailable(infoMessage: String) = onUiThread {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_new_feature_title))
                .setMessage(infoMessage)
                .setPositiveButton(getString(R.string.button_ok)) { dialog, _ -> dialog.dismiss() }
                .setOnDismissListener { }
                .setCancelable(false)
                .show()
    }


    override fun navigateToRentalDetailsPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(RentalDetailViewImpl::class, FLAG_ACTIVITY_NEW_TASK, preparationHandler)
    }


    /*------------------------------------- Private methods --------------------------------------*/


    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition(ParallelAutoTransition()
                    .excludeChildren(content, true)
                    .excludeChildren(bottomNavigationView, true)
            )
        }

        badge.number = totalNumberOfOrderedMachines
        chatButton.marginBottom = if (activeView == cartView && totalNumberOfOrderedMachines > 0) dp(48) else dp(0)
        chatButton.numberOfUnreadMessages = numberOfUnreadMessages

        noInternetMessageView.isVisible = isDisconnectedFromInternet
        chatButton.isVisible = isChatEnabled

        phoneCallButton.isVisible = isPhoneCallEnable
        phoneCallButton.phoneNumber = rentalcontactNumber

        showAccountTab(show = isAccess4UAvailable)
        updateViewForActiveTabIfNeeded()
    }

    private fun updateViewForActiveTabIfNeeded() {
        val view = viewForTab(activeTab)
        if (content.getChildAt(0) != view) {
            content.removeAllViews()
            content.addView(view)
            bottomNavigationView.selectedItemId = idForTab(activeTab)
        }
    }

    private fun viewForTab(tab: Tab): BaseView = when (tab) {
        SEARCH -> searchView
        CART -> cartView
        ACCOUNT -> accountView
        MORE -> moreView
    }

    private fun idForTab(tab: Tab) = when (tab) {
        SEARCH -> R.id.nav_search
        CART -> R.id.nav_cart
        ACCOUNT -> R.id.nav_account
        MORE -> R.id.nav_more
    }

    private fun showAccountTab(show: Boolean) {
        val accountTab = bottomNavigationView.menu.findItem(R.id.nav_account)
        if (show != accountTab.isVisible) {
            accountTab.isVisible = show
        }

        val cartTab = bottomNavigationView.navigationItemAt(1)
        if (badge.parent != null) {
            badge.removeFromParent()
        }
        cartTab.addView(badge)
    }

    private fun navigateToPlayStore() {

        val intent = when {
            FLAVOR_environment == "acceptance" -> packageManager.getLaunchIntentForPackage("io.crash.air")
            FLAVOR_brand == "riwal" -> openPlayStoreAppDetailsIntent(appId = "com.riwal.rentalapp")
            FLAVOR_brand == "manlift" -> openPlayStoreAppDetailsIntent(appId = "com.manlift.rentalapp")
            else -> null
        }

        if (intent != null) {
            startActivity(intent)
        }
    }

    private fun navigateToPlayStore(updateAppURL: String){
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(updateAppURL)))
    }

    private fun openPlayStoreAppDetailsIntent(appId: String) = openUrlIntent("https://play.google.com/store/apps/details?id=$appId")

    private fun openUrlIntent(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url))
}
