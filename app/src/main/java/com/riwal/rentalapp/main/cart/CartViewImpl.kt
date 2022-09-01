package com.riwal.rentalapp.main.cart

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.transition.AutoTransition
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.addaccessories.AddAccessoriesViewImpl
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.common.ui.transition.ModalPushActivityTransition
import com.riwal.rentalapp.common.ui.transition.PushActivityTransition
import com.riwal.rentalapp.contactform.ContactFormViewImpl
import com.riwal.rentalapp.model.MachineOrder
import kotlinx.android.synthetic.main.row_machine_order.view.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_cart.view.*

class CartViewImpl @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseView(context, attrs, defStyleAttr), CartView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: CartView.DataSource
    override lateinit var delegate: CartView.Delegate

    private val machineOrders: List<MachineOrder>
        get() = dataSource.machineOrders(view = this)

    private val isAddAccessoriesEnable
        get() = dataSource.isAddAccessoriesEnable(view = this)

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        addSubview(R.layout.view_cart)

        toolbar.title = getString(R.string.page_cart)

        submitButton.setOnClickListener { delegate.onSubmitButtonClicked() }

        recyclerView.dataSource = this
        recyclerView.delegate = this
    }

    override fun onAppear() {
        super.onAppear()
        updateUI(animated = false)
    }


    /*------------------------------------- CartView methods -------------------------------------*/


    override fun navigateToContact() {
        startActivity(ContactFormViewImpl::class, transition = ModalPushActivityTransition)
    }

    override fun navigateToAccessoriesPanel(preparationHandler: ControllerPreparationHandler) {
        startActivity(AddAccessoriesViewImpl::class, transition = PushActivityTransition, controllerPreparationHandler = preparationHandler)

    }

    override fun showDeleteMachineOrderConfirmation(machineOrder: MachineOrder) = runOnUiThread {
        AlertDialog.Builder(activity)
                .setTitle(getString(R.string.dialog_delete_machine_order_title))
                .setMessage(getString(R.string.dialog_delete_machine_order_message))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(getString(R.string.delete)) { _, _ -> delegate.onDeleteMachineOrderConfirmed(machineOrder) }
                .show()
    }

    override fun notifyCountryChanged() = runOnUiThread {
        updateUI()
    }


    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = machineOrders.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_machine_order
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = MachineOrderRowViewHolder(itemView, delegate)
    override fun stableIdForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = System.identityHashCode(machineOrders[indexPath.row]).toLong()


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {

        viewHolder as MachineOrderRowViewHolder

        val machineOrder = machineOrders[indexPath.row]
        val view = viewHolder.itemView

        viewHolder.updateWith(machineOrder, isAddAccessoriesEnable)

        view.decreaseQuantityButton.isEnabled = dataSource.canDecreaseQuantity(view = this, machineOrder = machineOrder)
        view.decreaseQuantityButton.setOnClickListener {
            delegate.onDecreaseQuantityButtonClicked(machineOrder)
        }

        view.increaseQuantityButton.setOnClickListener {
            delegate.onIncreaseQuantityButtonClicked(machineOrder)
        }

        view.deleteMachineOrderButton.setOnClickListener {
            delegate.onDeleteMachineOrderButtonClicked(machineOrder)
        }

        view.addAccessoriesButton.setOnClickListener {
            delegate.onAddAccessoriesClicked(machineOrder)
        }

    }


    /*-------------------------------------- Methods ---------------------------------------------*/


    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition(AutoTransition())
        }

        recyclerView.notifyDataSetChanged()
        emptyCartView.isVisible = machineOrders.isEmpty()
        recyclerView.isVisible = machineOrders.isNotEmpty()
        submitButton.isVisible = machineOrders.isNotEmpty()

    }

}
