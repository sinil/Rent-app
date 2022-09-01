package com.riwal.rentalapp.reportbreakdownform

import android.view.Menu
import android.view.MenuItem
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.items
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.reportbreakdownform.ReportBreakdownFormView.DataSource
import com.riwal.rentalapp.reportbreakdownform.ReportBreakdownFormView.Delegate
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_report_breakdown.*


class ReportBreakdownViewImpl : RentalAppNotificationActivity(), ReportBreakdownFormView {

    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    private var doneMenuItem: MenuItem? = null

    private val rental
        get() = dataSource.rental(view = this)

    private val canSubmitBreakdown
        get() = dataSource.canSubmitBreakdown(view = this)

    private val userPhoneNumber
        get() = dataSource.contactPhoneNumber(view = this)


    /*----------------------------------------- Lifecycle ----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_report_breakdown)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close, tint = color(R.color.app_bar_text))

        updateUI(animated = false)

        contactPhoneNumberEditText.text = userPhoneNumber
        breakdownMessageEditText.onTextChangedListener = { text -> delegate.onBreakdownMessageChanged(view = this@ReportBreakdownViewImpl, breakdownMessage = text) }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_report_breakdown, menu)
        doneMenuItem = menu.findItem(R.id.doneItem)

        menu.items.forEach { it.icon?.setTintList(R.color.toolbar_button) }
        title = getString(R.string.breakdown_title)
        fleetNumberTextView.text = rental.fleetNumber

        updateUI(animated = false)

        return true
    }


    /*----------------------------------------- Actions ------------------------------------------*/


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            delegate.onBackPressed(view = this); true
        }
        R.id.doneItem -> {
            delegate.onSubmitBreakdownSelected(view = this,contactPhoneNumber = contactPhoneNumberEditText.text);
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        delegate.onBackPressed(view = this)
    }


    /*----------------------------------- Request Changes View -----------------------------------*/


    override fun navigateBack() = finish(transition = ModalPopActivityTransition)


    /*-------------------------------------- Private Methods -------------------------------------*/


    override fun updateUI(animated: Boolean) {
        doneMenuItem?.isEnabled = canSubmitBreakdown
    }
}