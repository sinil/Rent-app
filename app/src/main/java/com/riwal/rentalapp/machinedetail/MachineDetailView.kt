package com.riwal.rentalapp.machinedetail

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.ContactInfo
import com.riwal.rentalapp.model.Machine
import java.net.URL

interface MachineDetailView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun navigateToFullscreenImageGallery(preparationHandler: ControllerPreparationHandler)
    fun showOrderPanel(preparationHandler: ControllerPreparationHandler)
    fun navigateToArView(preparationHandler: ControllerPreparationHandler)
    fun showPDF(url: String)

    interface DataSource {
        fun machine(view: MachineDetailView): Machine
        fun isChatEnabled(view: MachineDetailView): Boolean
        fun numberOfUnreadMessages(view: MachineDetailView): Int
        fun canUseAr(view: MachineDetailView): Boolean
        fun canDownloadSpecifications(view: MachineDetailView): Boolean
        fun isPhoneCallEnable(view: MachineDetailView): Boolean
        fun rentalDeskContactInfo(view: MachineDetailView): List<ContactInfo>

    }

    interface Delegate {
        fun onAddToCartSelected()
        fun onBackButtonClicked()
        fun onImageSelected(imageIndex: Int)
        fun onWorkDiagramSelected()
        fun onArButtonClicked()
        fun onDownloadSpecificationSelected()
    }

}