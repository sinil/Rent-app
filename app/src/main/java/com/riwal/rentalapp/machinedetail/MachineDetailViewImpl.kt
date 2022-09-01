package com.riwal.rentalapp.machinedetail

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color.TRANSPARENT
import android.graphics.Color.WHITE
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType.FIT_CENTER
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.arcore.isArSupported
import com.riwal.rentalapp.common.extensions.core.format
import com.riwal.rentalapp.common.extensions.core.formatWithDigits
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.extensions.widgets.viewpager.onUserScroll
import com.riwal.rentalapp.common.ui.EasyViewPager
import com.riwal.rentalapp.common.ui.TableKeyValueRowViewHolder
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.common.ui.transition.ModalPushActivityTransition
import com.riwal.rentalapp.fullscreenimagegallery.FullscreenImageGalleryViewImpl
import com.riwal.rentalapp.machinedetail.ar.MachineArViewImpl
import com.riwal.rentalapp.model.Notification
import com.riwal.rentalapp.model.Notification.MACHINE_ADDED
import com.riwal.rentalapp.orderpanel.OrderPanelViewImpl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_machine_detail.*
import java.util.concurrent.TimeUnit.SECONDS

@SuppressLint("StringFormatMatches")
class MachineDetailViewImpl : RentalAppNotificationActivity(), MachineDetailView, EasyViewPager.DataSource, EasyViewPager.Delegate, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: MachineDetailView.DataSource
    override lateinit var delegate: MachineDetailView.Delegate

    private lateinit var autoChangePictureSubscription: Disposable

    private val machine
        get() = dataSource.machine(view = this)

    private val isChatEnabled
        get() = dataSource.isChatEnabled(view = this)

    private val isPhoneCallEnable
        get() = dataSource.isPhoneCallEnable(view = this)

    private val numberOfUnreadMessages
        get() = dataSource.numberOfUnreadMessages(view = this)

    private val images
        get() = machine.images

    private var orderRentalPanel: OrderPanelViewImpl? = null

    private val isRentalPanelOpen
        get() = orderRentalPanel?.isOpen ?: false

    private val rentalDeskContactNumber
        get() = dataSource.rentalDeskContactInfo(view = this)[0].phoneNumber

    private val specifications by lazy {
        mutableListOf(
                getString(R.string.key_brand) to machine.brand + " "+machine.model,
                getString(R.string.key_working_height) to getString(R.string.value_in_m, machine.workingHeight.formatWithDigits(2)),
                getString(R.string.key_working_outreach) to getString(R.string.value_in_m, machine.workingOutreach.formatWithDigits(2)),
                getString(R.string.key_lift_capacity) to getString(R.string.value_in_kg, machine.liftCapacity.toInt().format()),
                getString(R.string.key_propulsion) to getString(machine.propulsion.localizedNameRes),
                getString(R.string.key_dimensions) to getString(R.string.value_in_dimensions_m3, machine.length.formatWithDigits(2), machine.width.formatWithDigits(2), machine.height.formatWithDigits(2)),
                getString(R.string.key_platform_dimensions) to getString(R.string.value_in_dimensions_m2, machine.platformLength.formatWithDigits(2), machine.platformWidth.formatWithDigits(2)),
                getString(R.string.key_weight) to getString(R.string.value_in_kg, machine.weight.toInt().format())
        )
    }


    private val canUseAr: Boolean
        get() = dataSource.canUseAr(this)

    private val canDownloadSpecifications
        get() = dataSource.canDownloadSpecifications(this)

    override val snackbarContainer: View
        get() = contentCoordinator


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_machine_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))

        title = machine.rentalType
        if (machine.workingOutreach <= 0) {
            specifications.removeAll { it.first == getString(R.string.key_working_outreach) }
        }
        picturesViewPager.dataSource = this
        picturesViewPager.delegate = this
        picturesViewPager.onUserScroll { autoChangePictureSubscription.dispose() }

        autoChangePictureSubscription = Observable.interval(5, SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (images.isNotEmpty()) picturesViewPager.currentItem = (picturesViewPager.currentItem + 1) % images.size
                }

        specificationsRecyclerView.dataSource = this
        specificationsRecyclerView.delegate = this

        diagramImageView.loadImage(machine.diagram)
        diagramImageView.setOnClickListener { delegate.onWorkDiagramSelected() }

        addToCartButton.setOnClickListener { delegate.onAddToCartSelected() }

        arButton.setOnClickListener { delegate.onArButtonClicked() }

        downloadSpecificationButton.setOnClickListener { delegate.onDownloadSpecificationSelected() }

        updateUI(animated = false)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                delegate.onBackButtonClicked()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (isRentalPanelOpen) {
            orderRentalPanel?.navigateBack()
        } else {
            delegate.onBackButtonClicked()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        autoChangePictureSubscription.dispose()
    }

    override fun shouldDeferNotification(notification: Notification) = when (notification) {
        MACHINE_ADDED -> true
        else -> false
    }


    /*--------------------------------------- DetailView -----------------------------------------*/


    override fun navigateBack() = finish()

    override fun navigateToFullscreenImageGallery(preparationHandler: ControllerPreparationHandler) {
        startActivity(FullscreenImageGalleryViewImpl::class, controllerPreparationHandler = preparationHandler, transition = ModalPushActivityTransition)
    }

    override fun showOrderPanel(preparationHandler: ControllerPreparationHandler) {
        orderRentalPanel = OrderPanelViewImpl(this)
        orderRentalPanel!!.show(contentView, preparationHandler)
    }

    override fun navigateToArView(preparationHandler: ControllerPreparationHandler) {
        startActivity(MachineArViewImpl::class, controllerPreparationHandler = preparationHandler, transition = ModalPushActivityTransition)
    }

    override fun showPDF(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }


    /*------------------------------- Easy View Pager Data Source --------------------------------*/


    override fun numberOfPages(viewPager: EasyViewPager) = images.size

    override fun viewForPageType(viewPager: EasyViewPager, viewType: Int, parent: ViewGroup, inflater: LayoutInflater, position: Int) = ImageView(this)


    /*--------------------------------- Easy View Pager Delegate ---------------------------------*/


    override fun onPageCreated(viewPager: EasyViewPager, page: EasyViewPager.Page, position: Int) {

        val imageView = page.view as ImageView

        with(imageView) {
            isClickable = true
            scaleType = FIT_CENTER
            setOnClickListener { delegate.onImageSelected(position) }
            loadImage(images[position]) { drawable ->
                drawable as BitmapDrawable?
                backgroundColor = if (drawable?.bitmap?.getPixel(0, 0) == WHITE) WHITE else TRANSPARENT
            }
        }

    }


    /*------------------------------ Easy Recycler View Data Source ------------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = specifications.size

    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_table_key_value

    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = TableKeyValueRowViewHolder(itemView)


    /*-------------------------------- Easy Recycler View Delegate -------------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        viewHolder as TableKeyValueRowViewHolder
        viewHolder.keyValue = specifications[indexPath.row]
        viewHolder.itemView.backgroundColor = color(if (indexPath.row % 2 == 0) R.color.material_grey_300 else R.color.white)
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition()
        }

        rentalTypeTextView.text = machine.rentalType
        machineTypeTextView.setText(machine.type.localizedNameRes)

        machineTypeDescriptionTextView.setText(machine.type.descriptionRes)

        fuelPropulsionIconImageView.isVisible = !machine.canRunOnElectricity
        electricPropulsionIconImageView.isVisible = machine.canRunOnElectricity

        minimumWorkingHeightTextView.text = getString(R.string.value_in_meters, machine.workingHeight.format(maximumFractionDigits = 1))
        minimumWorkingOutreachTextView.text = getString(R.string.value_in_meters, machine.workingOutreach.format(maximumFractionDigits = 1))
        minimumWorkingOutreachTextView.isVisible = machine.workingOutreach > 0
        workingOutReachImageView.isVisible = machine.workingOutreach > 0
        propulsionTextView.setText(machine.propulsion.localizedNameRes)
        liftCapacityTextView.text = getString(R.string.value_in_kg, machine.liftCapacity.toInt().format())
        diagramImageView.isVisible = machine.diagram != null

        chatButton.isVisible = isChatEnabled
        chatButton.numberOfUnreadMessages = numberOfUnreadMessages
        phoneCallButton.isVisible = isPhoneCallEnable
        phoneCallButton.phoneNumber = rentalDeskContactNumber
        arButton.isVisible = isArSupported(this) && canUseAr

        downloadSpecificationButton.isVisible = canDownloadSpecifications
    }


}
