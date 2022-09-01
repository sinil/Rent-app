package com.riwal.rentalapp.machinedetail.ar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.addSubview
import com.riwal.rentalapp.common.extensions.android.getString
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import kotlinx.android.synthetic.main.view_ar_help.view.*


class ArHelpViewImpl @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr), ArHelpView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {

    override lateinit var delegate: ArHelpView.Delegate

    private var helpItems = listOf(
            getString(R.string.ar_help_item_lighting)!!,
            getString(R.string.ar_help_item_floor_reflectivity)!!,
            getString(R.string.ar_help_item_open_area)!!,
            getString(R.string.ar_help_item_movement_speed)!!
    )

    override fun onCreate() {
        super.onCreate()
        addSubview(R.layout.view_ar_help)

        helpItemsRecyclerView.dataSource = this
        helpItemsRecyclerView.delegate = this

        dismissButton.setOnClickListener { delegate.onDismissSelected(view = this) }
    }

    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = helpItems.size

    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_ar_help_item

    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = ArHelpItemViewHolder(itemView)

    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        viewHolder as ArHelpItemViewHolder
        viewHolder.text = helpItems[indexPath.row]
    }

}
