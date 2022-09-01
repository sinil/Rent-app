package com.riwal.rentalapp.model

import android.content.SharedPreferences
import com.freshchat.consumer.sdk.Freshchat
import com.google.firebase.iid.FirebaseInstanceId
import com.riwal.rentalapp.common.extensions.android.get
import com.riwal.rentalapp.common.extensions.android.set
import com.riwal.rentalapp.common.extensions.core.doInBackground
import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.common.extensions.firebase.getTokenAsync
import com.riwal.rentalapp.model.api.BackendClient
import kotlinx.coroutines.rx2.await

class SessionManager(
        private val backendClient: BackendClient,
        private val firebaseInstanceId: FirebaseInstanceId,
        private val preferences: SharedPreferences,
        private val freshChat: Freshchat
) {


    /*--------------------------------------- Properties -----------------------------------------*/

    private var observers: List<SessionManager.Observer> = emptyList()

    var user = preferences.user
        private set(value) {
            field = value
            preferences.user = user
            setFreshChatUser(user)
            notifyUserChanged()
        }

    val isLoggedIn
        get() = user != null && backendClient.isLoggedIn


    /*--------------------------------------- Lifecycle ------------------------------------------*/


    init {
        setFreshChatUser(user)

        // We do this here to at least retry to upload the token when the initial upload failed, e.g. due to no internet connection or a server issue.
        tryUpdateNotificationToken()
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    suspend fun login(email: String, password: String): User {

        val userFromLogin = backendClient.login(email, password)
        val customers = backendClient.getCustomers()

        check(customers.isNotEmpty()) { "No customers assigned to your account" }

        updateNotificationToken(email)

        user = userFromLogin.copy(customers = customers)

        return user!!
    }

    fun logout() {
        user = null
        backendClient.logout()
        deleteNotificationToken()
    }

    suspend fun resetPassword(email: String): Boolean {
        return backendClient.resetPassword(email)
    }

    fun setCurrentCustomer(customer: Customer) {
        user?.currentCustomer = customer
        this.user = user
    }

    fun onNewNotificationToken(token: String) {
        tryUpdateNotificationToken()
    }

    suspend fun getCustomers(): List<Customer> {
        val customers = backendClient.getCustomers()
        user?.customers = customers

        if (customers.isEmpty())
            logout()

        return customers
    }

    fun addObserver(observer: Observer) {
        observers = observers + observer
        observer.onCustomersUpdated(sessionManager = this, customers = user?.customers)
    }

    fun removeObserver(observer: Observer) {
        observers = observers - observer
    }
    /*------------------------------------ Private methods ---------------------------------------*/


    private fun setFreshChatUser(user: User?) {
        freshChat.user.apply {
            firstName = user?.name
            email = user?.email
            setPhone("", user?.phoneNumber)
        }
    }

    private fun tryUpdateNotificationToken() = doInBackground {
        if (!isLoggedIn) {
            return@doInBackground
        }

        try {
            updateNotificationToken(user!!.email)
        } catch (error: Exception) {
            // Ignore
        }
    }

    private suspend fun updateNotificationToken(email: String) {
        val token = firebaseInstanceId.getTokenAsync().await()
        val storedToken = preferences.pushToken

        if (token == storedToken) {
            return
        }

        backendClient.registerForNotifications(email, token)
        preferences.pushToken = token
    }

    private fun deleteNotificationToken() = doInBackground {
        firebaseInstanceId.deleteInstanceId()
    }

    private fun notifyUserChanged() {
        postEvent(UserChangedEvent(user))
    }

    data class UserChangedEvent(val user: User?)


    /*--------------------------------------- Extensions -----------------------------------------*/


    private var SharedPreferences.pushToken: String?
        get() = this["storedPushToken"]
        set(value) {
            this["storedPushToken"] = value
        }


    /*--------------------------------------- Interfaces -----------------------------------------*/


    interface Observer {
        fun onCustomersUpdated(sessionManager: SessionManager, customers: List<Customer>?)
    }
}