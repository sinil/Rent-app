package com.riwal.rentalapp.main.more

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.addSubview
import com.riwal.rentalapp.common.extensions.android.getString
import com.riwal.rentalapp.common.extensions.android.startActivity
import com.riwal.rentalapp.common.extensions.widgets.setTintFromResource
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.common.ui.easyrecyclerview.MaterialRowViewHolder
import com.riwal.rentalapp.common.ui.easyrecyclerview.MaterialRowViewHolder.ImageStyle.SQUARE_SMALL
import com.riwal.rentalapp.contactinfo.ContactInfoViewImpl
import com.riwal.rentalapp.country.CountryViewImpl
import com.riwal.rentalapp.depot.DepotViewImpl
import com.riwal.rentalapp.feedback.FeedbackViewImpl
import com.riwal.rentalapp.main.more.MoreView.DataSource
import com.riwal.rentalapp.main.more.MoreView.Delegate
import com.riwal.rentalapp.main.more.MoreViewImpl.Action.*
import com.riwal.rentalapp.training.trainingtypes.TrainingTypesViewImpl
import com.riwal.rentalapp.webview.WebViewImpl
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_settings.view.*

class MoreViewImpl @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseView(context, attrs, defStyleAttr), MoreView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    private val sections = listOf(SECTION_SETTINGS, SECTION_INFO)
    private val settingActions = listOf(COUNTRY)
    private val infoActions = listOf(DEPOTS, CONTACT, DISCLAIMER, PRIVACY_POLICY, FEEDBACK)

    private val activeCountry
        get() = dataSource.country(view = this)

    private val appVersion
        get() = dataSource.appVersion(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        addSubview(R.layout.view_settings)

        toolbar.title = getString(R.string.page_more)

        actionsRecyclerView.dataSource = this
        actionsRecyclerView.delegate = this

        updateUI()
    }


    /*----------------------------------- ContactView methods ------------------------------------*/


    override fun navigateToPickCountryPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(CountryViewImpl::class, preparationHandler)
    }

    override fun navigateToWebView(preparationHandler: ControllerPreparationHandler) {
        startActivity(WebViewImpl::class, preparationHandler)
    }

    override fun navigateToPlusPortWebView(preparationHandler: ControllerPreparationHandler) {
        startActivity(WebViewImpl::class, preparationHandler)
    }

    override fun navigateToBookTrainingPage() {
        startActivity(TrainingTypesViewImpl::class)

    }


    /*------------------------------ Easy RecyclerView Data Source -------------------------------*/


    override fun numberOfSections(recyclerView: EasyRecyclerView) =  sections.size
    override fun titleForHeaderInSection(section: Int) = if (section == SECTION_SETTINGS) getString(R.string.settings)  else getString(R.string.information)
    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = if (section == SECTION_SETTINGS) settingActions.size  else infoActions.size


    /*-------------------------------- Easy RecyclerView Delegate --------------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        viewHolder as MaterialRowViewHolder

        val section = indexPath.section
        val action = if (section == SECTION_SETTINGS) settingActions[indexPath.row] else infoActions[indexPath.row]

        viewHolder.setTitle(action.nameRes)
        viewHolder.setImage(action.iconRes, SQUARE_SMALL)

        if (action == COUNTRY) {
            viewHolder.setAccessoryIcon(activeCountry.picture)
        }

        viewHolder.itemView.setBackgroundResource(R.color.white)
        viewHolder.imageView.setTintFromResource(R.color.colorPrimary)
    }

    override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {

        val section = indexPath.section

        when (if (section == SECTION_SETTINGS) settingActions[indexPath.row] else infoActions[indexPath.row]) {
            COUNTRY -> delegate.onPickCountrySelected()
            DEPOTS -> navigateToDepotPage()
            CONTACT -> navigateToContactPage()
            DISCLAIMER -> delegate.onDisclaimerSelected()
            PRIVACY_POLICY -> delegate.onPrivacyPolicySelected()
            FEEDBACK -> navigateToFeedbackPage()
        }
    }


    /*------------------------------------- Private methods --------------------------------------*/


    override fun updateUI(animated: Boolean) {
        actionsRecyclerView.notifyDataSetChanged()
        appVersionTextView.text = appVersion.versionString
    }

    private fun navigateToDepotPage() = startActivity(DepotViewImpl::class)
    private fun navigateToContactPage() = startActivity(ContactInfoViewImpl::class)
    private fun navigateToFeedbackPage() = startActivity(FeedbackViewImpl::class)


    /*------------------------------- Classes / Interfaces / Enums -------------------------------*/


    enum class Action(@StringRes val nameRes: Int, @DrawableRes val iconRes: Int) {
        COUNTRY(R.string.setting_country, R.drawable.ic_country),
        DEPOTS(R.string.setting_depots, R.drawable.ic_depot),
        CONTACT(R.string.setting_contact, R.drawable.ic_phone),
        DISCLAIMER(R.string.setting_disclaimer, R.drawable.ic_disclaimer),
        PRIVACY_POLICY(R.string.setting_privacy_policy, R.drawable.ic_privacy),
        FEEDBACK(R.string.setting_feedback, R.drawable.ic_feedback)
    }


    companion object {
        private const val SECTION_SETTINGS = 0
        private const val SECTION_INFO = 1
    }
}