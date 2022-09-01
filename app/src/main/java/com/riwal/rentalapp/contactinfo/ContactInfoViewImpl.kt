package com.riwal.rentalapp.contactinfo

import android.view.MenuItem
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.contactinfo.ContactInfoView.DataSource
import com.riwal.rentalapp.contactinfo.ContactInfoView.Delegate
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_contact.*


class ContactInfoViewImpl : RentalAppNotificationActivity(), ContactInfoView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    private val contactInfoList
        get() = dataSource.contactInfoList(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_contact)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = getString(R.string.page_contact)

        contactRecyclerView.dataSource = this
        contactRecyclerView.delegate = this
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


    override fun navigateBack() = finish()


    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int): Int {
        return contactInfoList.size
    }

    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath): Int {
        return R.layout.row_contact_info
    }

    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int): EasyRecyclerView.ViewHolder {
        return ContactInfoRowViewHolder(itemView)
    }


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val contactInfo = contactInfoList[indexPath.row]
        viewHolder as ContactInfoRowViewHolder
        viewHolder.updateWith(contactInfo)
    }

}