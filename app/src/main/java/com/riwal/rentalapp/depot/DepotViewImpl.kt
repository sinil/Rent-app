package com.riwal.rentalapp.depot

import android.view.MenuItem
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.depot.DepotView.DataSource
import com.riwal.rentalapp.depot.DepotView.Delegate
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_depot.*

class DepotViewImpl : RentalAppNotificationActivity(), DepotView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    private val depots
        get() = dataSource.depots(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_depot)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = getString(R.string.page_depot)

        depotRecyclerView.dataSource = this
        depotRecyclerView.delegate = this

        updateUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> delegate.onNavigateBackSelected()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        delegate.onNavigateBackSelected()
    }


    /*---------------------------------------- DepotView -----------------------------------------*/


    override fun navigateBack() {
        finish()
    }


    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int): Int {
        return depots.size
    }

    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath): Int {
        return R.layout.row_depot
    }

    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int): EasyRecyclerView.ViewHolder {
        return DepotRowViewHolder(itemView)
    }


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val depot = depots[indexPath.row]
        viewHolder as DepotRowViewHolder
        viewHolder.updateWith(depot)
    }


    /*-------------------------------------- Private methods -------------------------------------*/


    override fun updateUI(animated: Boolean) {
        depotRecyclerView.isVisible = depots.isNotEmpty()
        emptyDepotView.isVisible = depots.isEmpty()
    }

}