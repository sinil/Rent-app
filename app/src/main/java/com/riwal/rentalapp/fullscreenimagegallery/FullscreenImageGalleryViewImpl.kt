package com.riwal.rentalapp.fullscreenimagegallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.extensions.widgets.viewpager.DepthPageTransformer
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.fullscreenimagegallery.FullscreenImageGalleryView.DataSource
import kotlinx.android.synthetic.main.view_fullscreen_image.view.*
import kotlinx.android.synthetic.main.view_fullscreen_images.*

class FullscreenImageGalleryViewImpl : RentalAppNotificationActivity(), FullscreenImageGalleryView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource

    private val imageUrls
        get() = dataSource.imageURLs(view = this)

    private val activeImageIndex
        get() = dataSource.defaultActiveImageIndex(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_fullscreen_images)

        viewPager.adapter = FullscreenImagesPagerAdapter()
        viewPager.currentItem = activeImageIndex
        viewPager.setPageTransformer(true, DepthPageTransformer())
    }

    override fun onBackPressed() {
        finish(transition = ModalPopActivityTransition)
    }


    /*---------------------------------------- Inner classes -------------------------------------*/


    inner class FullscreenImagesPagerAdapter : PagerAdapter() {

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val imageUrl = imageUrls[position]
            val inflater = LayoutInflater.from(this@FullscreenImageGalleryViewImpl)
            val view = inflater.inflate(R.layout.view_fullscreen_image, collection, false)
            view.iconImageView.loadImage(imageUrl)
            collection.addView(view)
            return view
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        override fun isViewFromObject(view: View, `object`: Any) = view == `object`

        override fun getCount() = imageUrls.size

    }
}
