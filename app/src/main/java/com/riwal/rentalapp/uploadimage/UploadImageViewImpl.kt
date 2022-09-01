package com.riwal.rentalapp.uploadimage

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import com.opensooq.supernova.gligar.GligarPicker
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.RentalDialogs
import com.riwal.rentalapp.common.extensions.android.backgroundColor
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.extensions.widgets.viewpager.onUserScroll
import com.riwal.rentalapp.common.ui.EasyViewPager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_upload_image.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class UploadImageViewImpl : RentalAppNotificationActivity(), UploadImageView, EasyViewPager.DataSource, EasyViewPager.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    private var images: Array<String>? = null
    override lateinit var dataSource: UploadImageView.DataSource
    override lateinit var delegate: UploadImageView.Delegate

    private lateinit var autoChangePictureSubscription: Disposable
    private val PICKER_REQUEST_CODE = 30

    private val canUploadFile
        get() = dataSource.canUploadFile(view = this)

    private val isUploadingImage
        get() = dataSource.isUploadingImages(view = this)


    /*----------------------------------------- Lifecycle ----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_upload_image)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = getString(R.string.upload_image)

        addPhotoButton.setOnClickListener { onAddImageButtonPressed() }
        uploadButton.setOnClickListener { delegate.onSelectUploadImage(view = this) }

        picturesViewPager.dataSource = this
        picturesViewPager.delegate = this
        picturesViewPager.onUserScroll { autoChangePictureSubscription.dispose() }


        updateUI(animated = false)

    }

    override fun updateUI(animated: Boolean) {
        super.updateUI(animated)

        if (isUploadingImage) {
            activityIndicator.isVisible = true
            launch { delegate.uploadImage() }
        } else {
            activityIndicator.isVisible = false
        }


        if (!images.isNullOrEmpty() && !this::autoChangePictureSubscription.isInitialized) {
            autoChangePictureSubscription = Observable.interval(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        picturesViewPager.currentItem = (picturesViewPager.currentItem + 1) % images!!.size
                    }
        }

        uploadButton.isEnabled = canUploadFile

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navigateBack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::autoChangePictureSubscription.isInitialized)
            autoChangePictureSubscription.dispose()
    }

    /*----------------------------------------- Actions ------------------------------------------*/


    override fun showConformationDialog(positiveCallback: () -> Unit, negativeCallback: () -> Unit) {
        RentalDialogs.actionDialog(this, getString(R.string.upload_confirmation_dialog_message), getString(R.string.upload_image), getString(R.string.confirm), false, positiveCallback, negativeCallback).show()
        delegate.canUploadButtonClick(false)

    }

    override fun navigateBack() = runOnUiThread { finish() }

    private fun onAddImageButtonPressed() {
            GligarPicker()
                    .requestCode(PICKER_REQUEST_CODE)
                    .limit(4)
                    .withActivity(this)
                    .show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            PICKER_REQUEST_CODE -> {
                if(this::autoChangePictureSubscription.isInitialized)
                    autoChangePictureSubscription.dispose()

                images = data?.extras?.getStringArray(GligarPicker.IMAGES_RESULT)// return list of selected images paths.
                delegate.selectedImages(images)
                picturesViewPager.notifyDataSetChanged()
                picturesViewPager.flag_refresh = true
                delegate.canUploadButtonClick(true)

            }
        }
    }

    /*------------------------------- Easy View Pager Data Source --------------------------------*/


    override fun numberOfPages(viewPager: EasyViewPager) = images?.size ?: 0

    override fun viewForPageType(viewPager: EasyViewPager, viewType: Int, parent: ViewGroup, inflater: LayoutInflater, position: Int) = ImageView(this)


    /*--------------------------------- Easy View Pager Delegate ---------------------------------*/


    override fun onPageCreated(viewPager: EasyViewPager, page: EasyViewPager.Page, position: Int) {

        val imageView = page.view as ImageView

        with(imageView) {
            scaleType = ImageView.ScaleType.FIT_CENTER
            loadImage(images?.get(position)) { drawable ->
                drawable as BitmapDrawable?
                backgroundColor = if (drawable?.bitmap?.getPixel(0, 0) == Color.WHITE) Color.WHITE else Color.TRANSPARENT
            }
        }

    }


}