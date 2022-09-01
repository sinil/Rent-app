package com.riwal.rentalapp.fullscreenimagegallery


class FullscreenImageGalleryController(val view: FullscreenImageGalleryView) : FullscreenImageGalleryView.DataSource {


    /*---------------------------------------- Properties ----------------------------------------*/


    var imageUrls = emptyList<String>()
    var defaultActiveImageIndex: Int = 0


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
    }


    /*--------------------------- FullscreenImageGalleryView DataSource --------------------------*/


    override fun imageURLs(view: FullscreenImageGalleryView) = imageUrls
    override fun defaultActiveImageIndex(view: FullscreenImageGalleryView) = defaultActiveImageIndex

}