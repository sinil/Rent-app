package com.riwal.rentalapp.addaccessories

import android.view.MenuItem
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.common.ui.transition.PopActivityTransition
import com.riwal.rentalapp.model.AccessoryOrder
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_add_accessories.*

class AddAccessoriesViewImpl : RentalAppNotificationActivity(), AddAccessoriesView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    override lateinit var dataSource: AddAccessoriesView.DataSource
    override lateinit var delegate: AddAccessoriesView.Delegate

    private val accessories: List<AccessoryOrder>
        get() = dataSource.accessories(view = this)

    var isLoading = false
        get() = dataSource.isLoading(view = this)

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        setContentView(R.layout.view_add_accessories)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))

        title = getString(R.string.accessories_title)

        confirmButton.setOnClickListener { delegate.onConfirmClicked() }

        accessoriesRecyclerView.dataSource = this
        accessoriesRecyclerView.delegate = this

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


    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = accessories.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath): Int = R.layout.row_add_accessory
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int): EasyRecyclerView.ViewHolder = AccessoryRowViewHolder(itemView, this, delegate)


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {

        viewHolder as AccessoryRowViewHolder

        val accessory = accessories[indexPath.row]
        viewHolder.updateWith(accessory)


    }


    /*-------------------------------- Accessories Panel View -------------------------------------*/


    override fun navigateBack() = finish(transition = PopActivityTransition)


    /*-------------------------------------- Private methods -------------------------------------*/


    override fun updateUI(animated: Boolean) {

        accessoriesRecyclerView.notifyDataSetChanged()

        accessoriesProgressBar.isVisible = isLoading
        accessoriesRecyclerView.isVisible = !isLoading
    }
}