package com.riwal.rentalapp.fullscreenimagegallery

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView

interface FullscreenImageGalleryView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource

    interface DataSource {
        fun imageURLs(view: FullscreenImageGalleryView): List<String>
        fun defaultActiveImageIndex(view: FullscreenImageGalleryView): Int
    }

}