package com.riwal.rentalapp.uploadimage

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import javax.security.auth.callback.Callback

interface UploadImageView : MvcView, ObservableLifecycleView  {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun showConformationDialog(positiveCallback: () -> Unit,negativeCallback: () -> Unit)

    interface DataSource {
        fun isUploadingImages(view: UploadImageView) : Boolean
        fun canUploadFile(view: UploadImageView): Boolean

    }

    interface Delegate {
        fun onBackButtonClicked()
        fun onSelectUploadImage(view: UploadImageView)
        fun selectedImages(selectedImages: Array<String>?)
        fun canUploadButtonClick(isClick: Boolean)
        fun uploadImage()
    }
}