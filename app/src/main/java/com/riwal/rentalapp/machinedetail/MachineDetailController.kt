package com.riwal.rentalapp.machinedetail

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.BetterBundle
import com.riwal.rentalapp.common.extensions.rxjava.plusAssign
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.fullscreenimagegallery.FullscreenImageGalleryController
import com.riwal.rentalapp.machinedetail.ar.MachineArController
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.DocumentType.SPECIFICATION
import com.riwal.rentalapp.orderpanel.OrderPanelController
import io.reactivex.disposables.CompositeDisposable
import java.util.*


class MachineDetailController(
        val view: MachineDetailView,
        val orderManager: OrderManager,
        val chatManager: ChatManager,
        val analytics: RentalAnalytics,
        val locale: Locale,
        val countryManager: CountryManager) : MachineDetailView.DataSource, MachineDetailView.Delegate, ViewLifecycleObserver, OrderPanelController.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    lateinit var machine: Machine

    private var subscriptions = CompositeDisposable()

    private val hasArModels
        get() = machine.meshes.isNotEmpty()

    private val specificationDocuments
        get() = machine.documents.filter { it.type == SPECIFICATION }

    private val localizedSpecificationDocument
        get() = specificationDocuments.firstOrNull { it.language == locale.language }

    private val defaultSpecificationDocument
        get() = specificationDocuments.firstOrNull { it.language == "en" }

    private val specificationDocument
        get() = (localizedSpecificationDocument ?: defaultSpecificationDocument)

    private val hasSpecificationDocument
        get() = specificationDocument != null


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewAppear() {
        super.onViewAppear()
        subscriptions = CompositeDisposable()
        subscriptions += chatManager.observableNumberOfUnreadMessages.subscribe {
            view.notifyDataChanged()
        }
    }

    override fun onViewDisappear() {
        super.onViewDisappear()
        subscriptions.dispose()
    }

    override fun onViewSave(state: BetterBundle) {
        super.onViewSave(state)
        state["machine"] = machine
    }

    override fun onViewRestore(savedState: BetterBundle) {
        super.onViewRestore(savedState)
        machine = savedState["machine"]!!
    }


    /*----------------------------- Detail View Data Source methods ------------------------------*/


    override fun machine(view: MachineDetailView) = machine
    override fun isChatEnabled(view: MachineDetailView) = chatManager.isChatEnabled
    override fun numberOfUnreadMessages(view: MachineDetailView) = chatManager.numberOfUnreadMessages
    override fun canUseAr(view: MachineDetailView) = hasArModels
    override fun canDownloadSpecifications(view: MachineDetailView) = hasSpecificationDocument
    override fun isPhoneCallEnable(view: MachineDetailView): Boolean = countryManager.activeCountry!!.isPhoneCallEnable
    override fun rentalDeskContactInfo(view: MachineDetailView) = countryManager.activeCountry!!.contactInfo


    /*------------------------------- Detail View Delegate methods -------------------------------*/


    override fun onAddToCartSelected() {
        analytics.trackPlaceOrderEvent(machine)
        analytics.addToOrderSelected(machine)
        view.showOrderPanel { destination ->
            val controller = destination as OrderPanelController
            controller.machine = machine
            controller.delegate = this
        }
    }

    override fun onBackButtonClicked() {
        view.navigateBack()
    }

    override fun onImageSelected(imageIndex: Int) {
        view.navigateToFullscreenImageGallery { controller ->
            controller as FullscreenImageGalleryController
            controller.imageUrls = machine.images
            controller.defaultActiveImageIndex = imageIndex
        }
    }

    override fun onWorkDiagramSelected() {
        view.navigateToFullscreenImageGallery { controller ->
            controller as FullscreenImageGalleryController
            controller.imageUrls = listOf(machine.diagram!!)
        }
    }

    override fun onDownloadSpecificationSelected() = view.showPDF(specificationDocument!!.url)


    /*------------------------------- OrderPanelController Delegate ------------------------------*/


    override fun onOrderFinished(controller: OrderPanelController, machineOrder: MachineOrder) {
        analytics.machineAddedToCart(machineOrder)
        orderManager.addToCurrentOrder(machineOrder)
        view.navigateBack()
    }

    override fun onArButtonClicked() {
        analytics.viewInArSelected()
        view.navigateToArView { controller ->
            controller as MachineArController
            controller.machineMeshDescriptors = machine.meshes
        }
    }


}

