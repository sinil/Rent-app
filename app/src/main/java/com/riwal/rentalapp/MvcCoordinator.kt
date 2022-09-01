package com.riwal.rentalapp

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bumptech.glide.Glide
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.riwal.rentalapp.accessoriesonrent.AccessoriesOnRentController
import com.riwal.rentalapp.accessoriesonrent.AccessoriesOnRentView
import com.riwal.rentalapp.accessoriesonrentdetail.AccessoryOnRentDetailController
import com.riwal.rentalapp.accessoriesonrentdetail.AccessoryOnRentDetailView
import com.riwal.rentalapp.addaccessories.AddAccessoriesController
import com.riwal.rentalapp.addaccessories.AddAccessoriesView
import com.riwal.rentalapp.common.arcore.MaterialFactory
import com.riwal.rentalapp.common.extensions.core.doInBackground
import com.riwal.rentalapp.contactform.ContactFormController
import com.riwal.rentalapp.contactform.ContactFormView
import com.riwal.rentalapp.contactinfo.ContactInfoController
import com.riwal.rentalapp.contactinfo.ContactInfoView
import com.riwal.rentalapp.country.CountryController
import com.riwal.rentalapp.country.CountryView
import com.riwal.rentalapp.depot.DepotController
import com.riwal.rentalapp.depot.DepotView
import com.riwal.rentalapp.feedback.FeedbackController
import com.riwal.rentalapp.feedback.FeedbackView
import com.riwal.rentalapp.forgotpassword.ForgotPasswordController
import com.riwal.rentalapp.forgotpassword.ForgotPasswordView
import com.riwal.rentalapp.fullscreenimagegallery.FullscreenImageGalleryController
import com.riwal.rentalapp.fullscreenimagegallery.FullscreenImageGalleryView
import com.riwal.rentalapp.helpmechoose.HelpMeChooseWizardController
import com.riwal.rentalapp.helpmechoose.HelpMeChooseWizardView
import com.riwal.rentalapp.machinedetail.MachineDetailController
import com.riwal.rentalapp.machinedetail.MachineDetailView
import com.riwal.rentalapp.machinedetail.ar.FirestoreArModelLoader
import com.riwal.rentalapp.machinedetail.ar.MachineArController
import com.riwal.rentalapp.machinedetail.ar.MachineArView
import com.riwal.rentalapp.machinetransferpanel.MachineTransferPanelController
import com.riwal.rentalapp.machinetransferpanel.MachineTransferPanelView
import com.riwal.rentalapp.main.MainController
import com.riwal.rentalapp.main.MainView
import com.riwal.rentalapp.main.account.AccountController
import com.riwal.rentalapp.main.account.AccountView
import com.riwal.rentalapp.main.cart.CartController
import com.riwal.rentalapp.main.cart.CartView
import com.riwal.rentalapp.main.more.MoreController
import com.riwal.rentalapp.main.more.MoreView
import com.riwal.rentalapp.main.search.SearchController
import com.riwal.rentalapp.main.search.SearchView
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.api.BackendClient
import com.riwal.rentalapp.myinvoices.MyInvoicesController
import com.riwal.rentalapp.myinvoices.MyInvoicesView
import com.riwal.rentalapp.myprojects.MyProjectsController
import com.riwal.rentalapp.myprojects.MyProjectsView
import com.riwal.rentalapp.myprojects.machinetransfernotifications.TransferNotificationController
import com.riwal.rentalapp.myprojects.machinetransfernotifications.TransferNotificationView
import com.riwal.rentalapp.myquotations.MyQuotationsController
import com.riwal.rentalapp.myquotations.MyQuotationsView
import com.riwal.rentalapp.myrentals.MyRentalsController
import com.riwal.rentalapp.myrentals.MyRentalsView
import com.riwal.rentalapp.notifications.NotificationActionController
import com.riwal.rentalapp.notifications.NotificationActionService
import com.riwal.rentalapp.notifications.NotificationController
import com.riwal.rentalapp.notifications.NotificationService
import com.riwal.rentalapp.offrentpanel.OffRentPanelController
import com.riwal.rentalapp.offrentpanel.OffRentPanelView
import com.riwal.rentalapp.orderpanel.OrderPanelController
import com.riwal.rentalapp.orderpanel.OrderPanelView
import com.riwal.rentalapp.placepicker.PlacePickerController
import com.riwal.rentalapp.placepicker.PlacePickerView
import com.riwal.rentalapp.project.ProjectsController
import com.riwal.rentalapp.project.ProjectsView
import com.riwal.rentalapp.project.editproject.EditProjectController
import com.riwal.rentalapp.project.editproject.EditProjectView
import com.riwal.rentalapp.rentaldetail.RentalDetailController
import com.riwal.rentalapp.rentaldetail.RentalDetailView
import com.riwal.rentalapp.reportbreakdownform.ReportBreakdownFormController
import com.riwal.rentalapp.reportbreakdownform.ReportBreakdownFormView
import com.riwal.rentalapp.requestaccountform.RequestAccountFormController
import com.riwal.rentalapp.requestaccountform.RequestAccountFormView
import com.riwal.rentalapp.requestchangesform.ChangeRequestFormController
import com.riwal.rentalapp.requestchangesform.ChangeRequestFormView
import com.riwal.rentalapp.scanmachineqrcode.ScanMachineQRCodeController
import com.riwal.rentalapp.scanmachineqrcode.ScanMachineQRCodeView
import com.riwal.rentalapp.summary.SummaryController
import com.riwal.rentalapp.summary.SummaryView
import com.riwal.rentalapp.training.requesttrainingform.RequestTrainingFormController
import com.riwal.rentalapp.training.requesttrainingform.RequestTrainingFormView
import com.riwal.rentalapp.training.trainingdetail.TrainingDetailController
import com.riwal.rentalapp.training.trainingdetail.TrainingDetailView
import com.riwal.rentalapp.training.trainingtypes.TrainingTypesController
import com.riwal.rentalapp.training.trainingtypes.TrainingTypesView
import com.riwal.rentalapp.training.traininglist.TrainingListController
import com.riwal.rentalapp.training.traininglist.TrainingListView
import com.riwal.rentalapp.uploadimage.UploadImageController
import com.riwal.rentalapp.uploadimage.UploadImageView
import com.riwal.rentalapp.webview.WebView
import com.riwal.rentalapp.webview.WebViewController
import com.riwal.rentalapp.welcome.WelcomeView
import com.riwal.rentalapp.welcome.WelcomeViewController
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import kotlin.collections.set

typealias ControllerPreparationHandler = (Any) -> Unit

data class ViewCreatedEvent(val viewToken: Any, val view: Any)
data class ServiceCreatedEvent(val viewToken: Any, val service: Any)
data class RegisterControllerPreparationHandlerRequest(val viewToken: Any, val preparationHandler: ControllerPreparationHandler)

class MvcCoordinator(val context: Context) {


    /*---------------------------------------- Properties ----------------------------------------*/


    private val preferences = getSharedPreference()
    private val resources = context.resources!!
    private val freshchat = Freshchat.getInstance(context)!!
    private val analytics: RentalAnalytics
    private val backend: BackendClient

    private val sessionManager: SessionManager
    private val machinesManager: MachinesManager
    private val countryManager: CountryManager
    private val rentalManager: RentalManager
    private val orderManager: OrderManager
    private val projectsManager: ProjectsManager
    private val chatManager: ChatManager
    private val trainingManager: TrainingManager
    private val appVersionManager: AppVersionManager
    private val geocoder: Geocoder
    private val firebaseStorage = FirebaseStorage.getInstance()

    private val controllerPreparationHandlers: MutableMap<Any, ControllerPreparationHandler> = mutableMapOf()

    private val user
        get() = preferences.user

    private val currentCustomer
        get() = user?.currentCustomer

    private val activeCountry
        get() = countryManager.activeCountry

    private val locale
        get() = resources.configuration.locale


    /*----------------------------------------- Lifecycle ----------------------------------------*/


    init {

        val freshchatConfig = FreshchatConfig("e0a7bac8-4f0a-4dd2-94aa-a51f20e072cd", "3957dbdf-c944-45ca-9d57-a3fbc263c23e").apply {
            isCameraCaptureEnabled = true
            isGallerySelectionEnabled = true
        }
        try{freshchat.init(freshchatConfig)}catch (error:Exception){}


        doInBackground {
            Glide.get(context).clearDiskCache()
        }

        val preferencesMigrator = SharedPreferencesSchemaMigrator()
        preferencesMigrator.migratePreferencesIfNeeded(preferences)

        analytics = RentalAnalytics(context)
        backend = BackendClient(preferences, FirebaseAuth.getInstance(), GlideApp.with(context))
        sessionManager = SessionManager(backend, FirebaseInstanceId.getInstance(), preferences, freshchat)
        countryManager = CountryManager(locale, preferences)
        machinesManager = MachinesManager(backend, company = BuildConfig.COMPANY, countryManager = countryManager)
        rentalManager = RentalManager(sessionManager, backend, machinesManager)
        orderManager = OrderManager(sessionManager, backend, preferences)
        projectsManager = ProjectsManager(preferences)
        chatManager = ChatManager(freshchat, countryManager)
        trainingManager = TrainingManager(sessionManager,backend)
        appVersionManager = AppVersionManager(backend, preferences, activeCountry)
        geocoder = Geocoder(context)
        EventBus.getDefault().register(this)
    }


    /*------------------------------------------ Events ------------------------------------------*/


    @Subscribe
    fun onViewCreated(event: ViewCreatedEvent) {

        val view = event.view
        val token = event.viewToken
        val preparationHandler = controllerPreparationHandlers[token]

        controllerPreparationHandlers.remove(token)

        val controller: Any? = when (view) {
            is WelcomeView -> WelcomeViewController(view, countryManager)
            is CountryView -> CountryController(view, countryManager.countries, hasPendingOrder = orderManager.currentOrder.isNotEmpty())
            is MainView -> MainController(view, sessionManager, countryManager, orderManager, chatManager, appVersionManager)
            is RequestAccountFormView -> RequestAccountFormController(view)
            is ForgotPasswordView -> ForgotPasswordController(view, sessionManager)
            is SearchView -> SearchController(view, machinesManager, countryManager, resources, analytics)
            is HelpMeChooseWizardView -> HelpMeChooseWizardController(view, activeCountry!!, machinesManager, resources, analytics)
            is CartView -> CartController(view, activeCountry!!, orderManager, countryManager, analytics)
            is AddAccessoriesView -> AddAccessoriesController(view, activeCountry!!, orderManager, analytics)
            is AccountView -> AccountController(view, sessionManager, backend, rentalManager, countryManager, analytics)
            is MoreView -> MoreController(view, countryManager, orderManager, appVersionManager, resources)
            is ScanMachineQRCodeView -> ScanMachineQRCodeController(view, currentCustomer!!, rentalManager)
            is DepotView -> DepotController(view, activeCountry!!.depots)
            is ContactInfoView -> ContactInfoController(view, countryManager)
            is FeedbackView -> FeedbackController(view, rentalManager, activeCountry!!)
            is MachineDetailView -> MachineDetailController(view, orderManager, chatManager, analytics, locale, countryManager)
            is MachineArView -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val materialFactory = MaterialFactory(context)
                    val firestoreArModelLoader = FirestoreArModelLoader(context, firebaseStorage, materialFactory)
                    MachineArController(view, firestoreArModelLoader, analytics)
                } else {
                    null
                }
            }
            is ContactFormView -> ContactFormController(view, orderManager, analytics)
            is ProjectsView -> ProjectsController(view, orderManager, projectsManager, analytics)
            is EditProjectView -> EditProjectController(view)
            is SummaryView -> SummaryController(view, sessionManager, orderManager, activeCountry!!, analytics, resources)
            is MyRentalsView -> MyRentalsController(view, currentCustomer!!, activeCountry!!, rentalManager, chatManager, analytics, resources)
            is RentalDetailView -> RentalDetailController(view, currentCustomer!!, activeCountry!!, rentalManager, orderManager, machinesManager, backend, chatManager, geocoder, analytics)
            is ChangeRequestFormView -> ChangeRequestFormController(view, activeCountry!!.firstDayOfWeek)
            is OffRentPanelView -> OffRentPanelController(view, activeCountry!!)
            is OrderPanelView -> OrderPanelController(view, activeCountry!!, orderManager)
            is FullscreenImageGalleryView -> FullscreenImageGalleryController(view)
            is WebView -> WebViewController(view)
            is PlacePickerView -> PlacePickerController(view, geocoder, activeCountry!!)
            is ReportBreakdownFormView -> ReportBreakdownFormController(view)
            is AccessoriesOnRentView -> AccessoriesOnRentController(view, currentCustomer!!, activeCountry!!, rentalManager, analytics, chatManager, resources)
            is AccessoryOnRentDetailView -> AccessoryOnRentDetailController(view, currentCustomer!!, activeCountry!!, rentalManager, orderManager, chatManager, analytics)
            is MyProjectsView -> MyProjectsController(view, activeCountry!!, chatManager, analytics, orderManager, sessionManager, machinesManager)
            is MachineTransferPanelView -> MachineTransferPanelController(view, activeCountry!!)
            is TransferNotificationView -> TransferNotificationController(view, orderManager, activeCountry!!, sessionManager)
            is UploadImageView -> UploadImageController(view, orderManager, activeCountry!!)
            is TrainingTypesView -> TrainingTypesController(view,  activeCountry!!, trainingManager,chatManager, analytics)
            is TrainingListView -> TrainingListController(view,activeCountry!!,chatManager,analytics)
            is TrainingDetailView -> TrainingDetailController(view,activeCountry!!,chatManager,analytics)
            is RequestTrainingFormView -> RequestTrainingFormController(view, trainingManager, countryManager, analytics, resources, activeCountry!!)
            is MyInvoicesView -> MyInvoicesController(view, currentCustomer!!, rentalManager, chatManager, analytics, activeCountry !!)
            is MyQuotationsView -> MyQuotationsController(view, currentCustomer!!, rentalManager, analytics, activeCountry!!)
            else -> null
        }

        if (controller != null && preparationHandler != null) {
            preparationHandler(controller)
        }

    }

    @Subscribe
    fun onServiceCreated(event: ServiceCreatedEvent) {

        val service = event.service
        val token = event.viewToken
        val preparationHandler = controllerPreparationHandlers[token]

        controllerPreparationHandlers.remove(token)

        val controller: Any? = when (service) {
            is NotificationService -> NotificationController(service, sessionManager)
            is NotificationActionService -> NotificationActionController(service, sessionManager, analytics)
            else -> null
        }

        if (controller != null && preparationHandler != null) {
            preparationHandler(controller)
        }

    }

    @Subscribe
    fun onRegisterControllerPreparationHandlerRequested(request: RegisterControllerPreparationHandlerRequest) {
        val token = request.viewToken
        val handler = request.preparationHandler
        controllerPreparationHandlers[token] = handler
    }

    private fun getSharedPreference(): SharedPreferences {
        val masterKey =
                MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        return EncryptedSharedPreferences.create(
                context,
                "com.riwal.customerapp.sharedpreferences",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

}