package com.riwal.rentalapp

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event.*
import com.google.firebase.analytics.FirebaseAnalytics.Param.*
import com.riwal.rentalapp.common.extensions.android.toBundle
import com.riwal.rentalapp.common.extensions.datetime.isoString
import com.riwal.rentalapp.machinedetail.ar.MachineArView.OnboardingStep
import com.riwal.rentalapp.machinedetail.ar.MachineArView.OnboardingStep.*
import com.riwal.rentalapp.model.*
import org.json.JSONException
import org.json.JSONObject

class RentalAnalytics(val context: Context) {


    private val analytics = FirebaseAnalytics.getInstance(context)


    fun onBoardingStepInArCompleted(onBoardingStep: OnboardingStep) {
        when (onBoardingStep) {
            FLOOR_DETECTION -> {
                logEvent(MACHINE_PLACED_IN_AR)
                trackEvent(MACHINE_PLACED_IN_AR)
            }
            MACHINE_MOVEMENT -> {
                logEvent(MACHINE_MOVED_IN_AR)
                trackEvent(MACHINE_MOVED_IN_AR)
            }
            MACHINE_ROTATION -> {
                logEvent(MACHINE_ROTATED_IN_AR)
                trackEvent(MACHINE_ROTATED_IN_AR)
            }
            MACHINE_POSITIONING -> {
                logEvent(MACHINE_POSITION_CHANGED_IN_AR)
                trackEvent(MACHINE_POSITION_CHANGED_IN_AR)
            }
            DISPLAY_MODE_SWITCHING -> {
                logEvent(MEASURE_MODE_SELECTED_IN_AR)
                trackEvent(MEASURE_MODE_SELECTED_IN_AR)
            }
            WALL_DETECTION -> {
                logEvent(MACHINE_DIMENSIONS_PLACED_IN_AR)
                trackEvent(MACHINE_DIMENSIONS_PLACED_IN_AR)
            }
            MACHINE_DIMENSIONS_MOVEMENT -> {
                logEvent(MACHINE_DIMENSIONS_MOVED_IN_AR)
                trackEvent(MACHINE_DIMENSIONS_MOVED_IN_AR)
            }
            else -> {
                logEvent(MACHINE_DIMENSIONS_MOVED_IN_AR)
                trackEvent(MACHINE_DIMENSIONS_MOVED_IN_AR)
            }
        }
    }

    fun helpMeChooseResultsSelected() {
        logEvent(SELECT_HELP_ME_CHOOSE_RESULTS)
        trackEvent(SELECT_HELP_ME_CHOOSE_RESULTS)
    }

    fun viewInArSelected() {
        logEvent(SELECT_VIEW_IN_AR)
        trackEvent(SELECT_VIEW_IN_AR)
    }

    fun checkoutStart() {
        logEvent(BEGIN_CHECKOUT)
        trackEvent(BEGIN_CHECKOUT)
    }

    fun userLookingAtHelpMeChoosePage() {
        logEvent(DISPLAY_HELP_ME_CHOOSE)
        trackEvent(DISPLAY_HELP_ME_CHOOSE)
    }

    fun userLookingAtOrderContactForm() {
        logEvent(DISPLAY_ORDER_CONTACT_FORM)
        trackEvent(DISPLAY_ORDER_CONTACT_FORM)
    }

    fun userLookingAtProjectsPage() {
        logEvent(DISPLAY_PROJECTS)
        trackEvent(DISPLAY_PROJECTS)
    }

    fun userLookingAtOrderSummary() {
        logEvent(DISPLAY_ORDER_SUMMARY)
        trackEvent(DISPLAY_ORDER_SUMMARY)
    }

    fun checkoutComplete() {
        logEvent(CHECKOUT_COMPLETE)
        trackEvent(CHECKOUT_COMPLETE)
    }

    fun userLookingAtCart() {
        logEvent(DISPLAY_CART)
        trackEvent(DISPLAY_CART)
    }

    fun userLookingAtAccountPage() {
        logEvent(DISPLAY_ACCOUNT_PAGE)
        trackEvent(DISPLAY_ACCOUNT_PAGE)
    }

    fun userLookingAtMyRentals() {
        logEvent(DISPLAY_MY_RENTALS)
        trackEvent(DISPLAY_MY_RENTALS)
    }

    fun userLookingAtMyRentalsMapView() {
        trackEvent(DISPLAY_MY_RENTALS_MAP_VIEW)
    }

    fun confirmOffRentSelected() {
        logEvent(OFF_RENT_CONFIRMED)
        trackEvent(OFF_RENT_CONFIRMED)
    }

    fun searchResultsShownToUser(query: String) {
        logEvent(VIEW_SEARCH_RESULTS, mapOf(
                SEARCH_TERM to query
        ))

        val props = JSONObject()
        try {
            props.put(SEARCH_TERM, query)
        } catch (e: JSONException) {
        }

        trackEvent(VIEW_SEARCH_RESULTS, props)
    }

    fun addToOrderSelected(machine: Machine) {
        logEvent(SELECT_ADD_TO_ORDER, mapOf(
                ITEM_NAME to machine.rentalType
        ))

        val props = JSONObject()
        try {
            props.put(ITEM_NAME, machine)
        } catch (e: JSONException) {
        }

        trackEvent(SELECT_ADD_TO_ORDER, props)
    }

    fun machineAddedToCart(order: MachineOrder) {
        logEvent(ADD_TO_CART, mapOf(
                ITEM_NAME to order.machine.rentalType,
                START_DATE to order.onRentDate.isoString,
                END_DATE to order.offRentDate.isoString
        ))


        val props = JSONObject()
        try {
            props.put(ITEM_NAME, order.machine.rentalType)
            props.put(START_DATE, order.onRentDate.isoString)
            props.put(END_DATE, order.offRentDate.isoString)
        } catch (e: JSONException) {
        }
        trackEvent(ADD_TO_CART, props)

    }

    // Same as addToOrderSelected, just different event name
    fun rentAgainSelected(machine: Machine) {
        logEvent(SELECT_RENT_AGAIN, mapOf(
                ITEM_NAME to machine.rentalType,
                ITEM_CATEGORY to machine.type.name
        ))


        val props = JSONObject()
        try {
            props.put(ITEM_NAME, machine.rentalType)
            props.put(ITEM_CATEGORY, machine.type.name)
        } catch (e: JSONException) {
        }

        trackEvent(SELECT_RENT_AGAIN, props)
    }

    fun offRentAccessoriesSelected(accessories: AccessoriesOnRent) {
        logEvent(SELECT_OFF_RENT_ACCESSORIES, mapOf(
                ITEM_NAME to accessories.accessoryName
        ))


        val props = JSONObject()
        try {
            props.put(ITEM_NAME, accessories.accessoryName)
        } catch (e: JSONException) {
        }

        trackEvent(SELECT_OFF_RENT_ACCESSORIES, props)
    }

    fun offRentSelected(rental: Rental) {
        logEvent(SELECT_OFF_RENT, mapOf(
                ITEM_NAME to rental.rentalType
        ))

        val props = JSONObject()
        try {
            props.put(ITEM_NAME, rental.rentalType)
        } catch (e: JSONException) {
        }

        trackEvent(SELECT_OFF_RENT, props)
    }

    fun offRentMultipleSelected(rentals: List<Rental>) {
        logEvent(SELECT_OFF_RENT_MULTIPLE, mapOf(
                ITEM_LIST to rentals.map { it.rentalType }
        ))


    }

    fun offRentNotificationSelected(rental: Rental) {
        logEvent(SELECT_OFF_RENT_NOTIFICATION, mapOf(
                ITEM_NAME to rental.rentalType
        ))

        val props = JSONObject()
        try {
            props.put(ITEM_NAME, rental.rentalType)
        } catch (e: JSONException) {
        }

        trackEvent(SELECT_OFF_RENT_NOTIFICATION, props)
    }

    fun displayingAccessoriesOnRent() {
        logEvent(DISPLAY_ACCESSORIES_ON_RENT)
        trackEvent(DISPLAY_ACCESSORIES_ON_RENT)
    }

    fun displayingMyProjects() {
        logEvent(DISPLAY_MY_PROJECTS)
        trackEvent(DISPLAY_MY_PROJECTS)
    }

    fun displayingTrainingTypes() {
        logEvent(DISPLAY_TRAINING_TYPES)
        trackEvent(DISPLAY_TRAINING_TYPES)
    }

    fun displayingTrainingSubTypes(name: String) {
        logEvent(DISPLAY_TRAINING_SUB_TYPE)

        val props = JSONObject()
        try {
            props.put(TRAINING_SUB_TYPE,name)
        } catch (e: JSONException) {
        }
        trackEvent(DISPLAY_TRAINING_SUB_TYPE,props)
    }

    fun displayingTrainingDetails(name: String) {
        logEvent(DISPLAY_TRAINING_DETAILS)

        val props = JSONObject()
        try {
            props.put(TRAINING_NAME,name)
        } catch (e: JSONException) {
        }
        trackEvent(DISPLAY_TRAINING_DETAILS)
    }

    fun displayingMyInvoices() {
        logEvent(DISPLAY_MY_INVOICES)
        trackEvent(DISPLAY_MY_INVOICES)
    }

    fun displayingMyQuotation() {
        logEvent(DISPLAY_MY_QUOTATION)
        trackEvent(DISPLAY_MY_QUOTATION)
    }

    fun requestQuoteSelected() {
        logEvent(SELECT_REQUEST_QUOTE)
        trackEvent(SELECT_REQUEST_QUOTE)
    }

    fun sendRequestSelected() {
        logEvent(SELECT_SEND_REQUEST)
        trackEvent(SELECT_SEND_REQUEST)
    }
    fun requestTrainingConfirmed() {
        logEvent(REQUEST_TRAINING_CONFIRMED)
        trackEvent(REQUEST_TRAINING_CONFIRMED)
    }

    fun downloadInvoiceSelected() {
        logEvent(SELECT_DOWNLOAD_INVOICE)
        trackEvent(SELECT_DOWNLOAD_INVOICE)
    }

    fun downloadQuotationSelected() {
        logEvent(SELECT_DOWNLOAD_QUOTATION)
        trackEvent(SELECT_DOWNLOAD_QUOTATION)
    }

    fun adventGameSelected() {
        trackEvent(SELECT_ADVENT_GAME)
    }

    // ---------------------------------- Mixpanel Events ------------------------------------------

    fun trackPlaceOrderEvent(machine: Machine) {
        val props = JSONObject()
        try {
            props.put(MACHINE_BRAND, machine.brand)
            props.put(MACHINE_Type, machine.rentalType)
        } catch (e: JSONException) {
        }
        trackEvent(SELECT_ADD_TO_ORDER, props)
    }

    fun trackCheckOutEvent(country: Country) {
        val props = JSONObject()
        try {
            props.put(APP_COUNTRY, country)
        } catch (e: JSONException) {
        }
        trackEvent(CHECKOUT_COMPLETE, props)
    }

    fun trackRentAgainEvent(machine: Machine) {
        val props = JSONObject()
        try {
            props.put(MACHINE_BRAND, machine.brand)
            props.put(MACHINE_Type, machine.rentalType)
        } catch (e: JSONException) {
        }
        trackEvent(SELECT_RENT_AGAIN, props)
    }

    fun trackRemoveOrderEvent(machine: Machine) {
        val props = JSONObject()
        try {
            props.put(MACHINE_BRAND, machine.brand)
            props.put(MACHINE_Type, machine.rentalType)
        } catch (e: JSONException) {
        }
        trackEvent(SELECT_REMOVE_ORDER, props)
    }

    fun machineTransferSelected() {
        trackEvent(SELECT_MACHINE_TRANSFER)
    }

    fun trackMachineTransferCompleteEvent() {
        trackEvent(SELECT_MACHINE_TRANSFER_COMPLETE)
    }

    fun setUser(user: User?) {
        if (user != null) {
            val people = Application[context].mixpanel.people
            people.identify(Application[context].mixpanel.distinctId)
            people.set(EMAIL, user.email)
            people.set(PHONE, user.phoneNumber)
            people.set(NAME, user.name)
            if (user.currentCustomer != null) {
                people.set(CURRENT_CUSTOMER, user.currentCustomer?.name)
                people.set(CURRENT_CUSTOMER_ID, user.currentCustomer?.id)
            }
        }
    }


    fun trackDisplayMachineDetailEvent(machine: Machine) {
        val props = JSONObject()
        try {
            props.put(MACHINE_BRAND, machine.brand)
            props.put(MACHINE_Type, machine.rentalType)
        } catch (e: JSONException) {
        }
        trackEvent(Display_MACHINE_DETAIL, props)
    }


    private fun logEvent(name: String, params: Map<String, Any>? = null) = analytics.logEvent(name, params)

    private fun FirebaseAnalytics.logEvent(name: String, params: Map<String, Any>? = null) = logEvent(name, params?.toBundle())

    private fun trackEvent(name: String, params: JSONObject? = null) = Application[context].mixpanel.track(name, params)

    companion object {
        const val SELECT_HELP_ME_CHOOSE_RESULTS = "select_help_me_choose_results"
        const val SELECT_ADD_TO_ORDER = "select_add_to_order"
        const val DISPLAY_HELP_ME_CHOOSE = "display_help_me_choose"
        const val DISPLAY_CART = "display_cart"
        const val DISPLAY_ORDER_CONTACT_FORM = "display_order_contact_form"
        const val DISPLAY_PROJECTS = "display_projects"
        const val DISPLAY_ORDER_SUMMARY = "display_order_summary"
        const val CHECKOUT_COMPLETE = "checkout_complete"
        const val DISPLAY_ACCOUNT_PAGE = "display_account_page"
        const val DISPLAY_MY_RENTALS = "display_my_rentals"
        const val DISPLAY_MY_RENTALS_MAP_VIEW = "display_my_rentals_map_view"
        const val DISPLAY_ACCESSORIES_ON_RENT = "display_accessories_on_rent"
        const val SELECT_RENT_AGAIN = "select_rent_again"
        const val SELECT_OFF_RENT = "select_off_rent"
        const val SELECT_OFF_RENT_ACCESSORIES = "select_off_rent_accessories"
        const val SELECT_OFF_RENT_MULTIPLE = "select_off_rent_multiple"
        const val SELECT_OFF_RENT_NOTIFICATION = "select_off_rent_notification"
        const val OFF_RENT_CONFIRMED = "off_rent_confirmed"

        const val SELECT_VIEW_IN_AR = "select_view_in_ar"
        const val MACHINE_PLACED_IN_AR = "machine_placed_in_ar"
        const val MACHINE_MOVED_IN_AR = "machine_moved_in_ar"
        const val MACHINE_ROTATED_IN_AR = "machine_rotated_in_ar"
        const val MACHINE_POSITION_CHANGED_IN_AR = "machine_position_changed_in_ar"
        const val MEASURE_MODE_SELECTED_IN_AR = "measure_mode_selected_in_ar"
        const val MACHINE_DIMENSIONS_PLACED_IN_AR = "machine_dimensions_placed_in_ar"
        const val MACHINE_DIMENSIONS_MOVED_IN_AR = "machine_dimensions_moved_in_ar"
        const val SELECT_MACHINE_TRANSFER = "select_machine_transfer"
        const val SELECT_MACHINE_TRANSFER_COMPLETE = "machine_transfer_completed"

        const val Display_MACHINE_DETAIL = "display_machine_detail"
        const val SELECT_REMOVE_ORDER = "select_remove_order"

        const val DISPLAY_MY_PROJECTS = "display_my_projects"
        const val SELECT_TRANSFER_REQUEST = "select_transfer_request"
        const val SELECT_ACCEPT_TRANSFER = "select_accept_transfer"
        const val SELECT_REJECT_TRANSFER = "select_reject_transfer"
        const val DISPLAY_TRAINING_TYPES = "display_training_types"
        const val DISPLAY_TRAINING_SUB_TYPE = "display_training_sub_type"
        const val DISPLAY_TRAINING_DETAILS = "display_training_details"
        const val SELECT_REQUEST_QUOTE = "select_request_quote"
        const val SELECT_SEND_REQUEST = "select_send_request"
        const val REQUEST_TRAINING_CONFIRMED = "request_training_confirmed"
        const val DISPLAY_MY_INVOICES = "display_my_invoices"
        const val SELECT_DOWNLOAD_INVOICE = "select_download_invoice"
        const val DISPLAY_MY_QUOTATION = "display_my_quotations"
        const val SELECT_DOWNLOAD_QUOTATION = "select_download_quotation"
        const val SELECT_ADVENT_GAME = "select_advent_game"


        const val EMAIL = "email"
        const val PHONE = "phone"
        const val NAME = "name"
        const val CURRENT_CUSTOMER = "current_customer"
        const val CURRENT_CUSTOMER_ID = "current_customer_id"
        const val MACHINE_BRAND = "machine_brand"
        const val MACHINE_Type = "machine_type"
        const val APP_COUNTRY = "app_country"
        const val TRAINING_SUB_TYPE = "training_sub_type"
        const val TRAINING_NAME = "training_name"

    }

}
