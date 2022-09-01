package com.riwal.rentalapp.helpmechoose

import android.annotation.SuppressLint
import android.view.MenuItem
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.animation.excludeTargets
import com.riwal.rentalapp.common.extensions.widgets.scrollToBottom
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.mvc.BaseActivity
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.helpmechoose.HelpMeChooseWizardView.DataSource
import com.riwal.rentalapp.helpmechoose.HelpMeChooseWizardView.Delegate
import com.riwal.rentalapp.helpmechoose.LiftType.MATERIALS
import com.riwal.rentalapp.helpmechoose.LiftType.PEOPLE
import com.riwal.rentalapp.helpmechoose.Location.INDOORS
import com.riwal.rentalapp.helpmechoose.Location.OUTDOORS
import com.riwal.rentalapp.helpmechoose.ReachType.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_help_me_choose_wizard.*

class HelpMeChooseWizardViewImpl : BaseActivity(), HelpMeChooseWizardView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    private var liftType = LiftType.NOT_SPECIFIED
    private var location = Location.NOT_SPECIFIED
    private var reach = ReachType.NOT_SPECIFIED

    private lateinit var liftTypeOptionGroup: OptionGroup
    private lateinit var locationOptionGroup: OptionGroup
    private lateinit var reachOptionGroup: OptionGroup

    private val minimumWorkingHeight
        get() = dataSource.minimumWorkingHeight(view = this)

    private val minimumWorkingOutreach
        get() = dataSource.minimumWorkingOutreach(view = this)

    private val canSelectLocation
        get() = dataSource.canSelectLocation(view = this)

    private val canSelectReach
        get() = dataSource.canSelectReachType(view = this)

    private val canSelectWorkingHeight
        get() = dataSource.canSelectWorkingHeight(view = this)

    private val canSelectOutreach
        get() = dataSource.canSelectWorkingOutreach(view = this)

    private val canShowResults
        get() = dataSource.canShowResults(view = this)

    private val maximumWorkingHeight
        get() = dataSource.maximumWorkingHeight(view = this)

    private val maximumWorkingOutreach
        get() = dataSource.maximumWorkingOutreach(view = this)

    private val numberOfResults
        get() = dataSource.numberOfResults(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_help_me_choose_wizard)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))

        title = getString(R.string.help_me_choose_wizard_title)

        liftTypeOptionGroup = OptionGroup(liftPeopleButton, liftMaterialsButton)
        locationOptionGroup = OptionGroup(locationOutdoorsButton, locationIndoorsButton)
        reachOptionGroup = OptionGroup(verticalReachButton, angledReachButton, multiAngledReachButton)

        liftPeopleButton.setOnClickListener { delegate.onLiftTypeSelected(view = this, liftType = PEOPLE) }
        liftMaterialsButton.setOnClickListener { delegate.onLiftTypeSelected(view = this, liftType = MATERIALS) }

        locationOutdoorsButton.setOnClickListener { delegate.onLocationSelected(view = this, location = OUTDOORS) }
        locationIndoorsButton.setOnClickListener { delegate.onLocationSelected(view = this, location = INDOORS) }

        verticalReachButton.setOnClickListener { delegate.onReachTypeSelected(view = this, reachType = VERTICAL) }
        angledReachButton.setOnClickListener { delegate.onReachTypeSelected(view = this, reachType = ANGLED) }
        multiAngledReachButton.setOnClickListener { delegate.onReachTypeSelected(view = this, reachType = MULTI_ANGLED) }

        workingHeightSlider.setOnValueChangedListener { onWorkingHeightValueChanged() }
        workingOutreachSlider.setOnValueChangedListener { onWorkingOutreachValueChanged() }

        showResultsButton.setOnClickListener { delegate.onShowResultsSelected(view = this) }

        updateUI(animated = false)
    }

    override fun onBackPressed() {
        navigateBack()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /*----------------------------------------- Actions ------------------------------------------*/


    private fun onWorkingHeightValueChanged() {
        delegate.onMinimumWorkingHeightChanged(view = this, newValue = workingHeightSlider.value)
        updateUI(animated = false)
    }

    private fun onWorkingOutreachValueChanged() {
        delegate.onMinimumWorkingOutreachChanged(view = this, newValue = workingOutreachSlider.value)
        updateUI(animated = false)
    }


    /*---------------------------------- HelpMeChooseWizardView ----------------------------------*/


    override fun navigateBack() = finish()


    /*----------------------------------------- Methods ------------------------------------------*/


    @SuppressLint("StringFormatMatches")
    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition(ParallelAutoTransition()
                    .onTransitionEnd { scrollView.scrollToBottom() }
                    .excludeTargets(listOf(workingHeightSlider, workingHeightValueTextView, workingOutreachSlider, workingOutreachValueTextView)))
        }

        val liftType = dataSource.liftType(view = this)
        val location = dataSource.location(view = this)
        val reach = dataSource.reachType(view = this)

        val isAnyOptionChanged = this.liftType != liftType || this.location != location || this.reach != reach

        this.liftType = liftType
        this.location = location
        this.reach = reach

        liftTypeOptionGroup.selectedOption = when (liftType) {
            LiftType.NOT_SPECIFIED -> null
            PEOPLE -> liftPeopleButton
            MATERIALS -> liftMaterialsButton
        }

        locationOptionGroup.selectedOption = when (location) {
            Location.NOT_SPECIFIED -> null
            OUTDOORS -> locationOutdoorsButton
            INDOORS -> locationIndoorsButton
        }

        reachOptionGroup.selectedOption = when (reach) {
            ReachType.NOT_SPECIFIED -> null
            VERTICAL -> verticalReachButton
            ANGLED -> angledReachButton
            MULTI_ANGLED -> multiAngledReachButton
        }

        workingHeightSlider.value = minimumWorkingHeight
        workingHeightSlider.max = maximumWorkingHeight
        workingHeightValueTextView.text = if (minimumWorkingHeight == 0) getString(R.string.help_me_choose_slider_zero_placeholder) else getString(R.string.value_in_m, minimumWorkingHeight)
        workingHeightValueTextView.horizontalBias = workingHeightSlider.value.toFloat() / workingHeightSlider.max

        workingOutreachSlider.value = minimumWorkingOutreach
        workingOutreachSlider.max = maximumWorkingOutreach
        workingOutreachValueTextView.text = if (minimumWorkingOutreach == 0) getString(R.string.help_me_choose_slider_zero_placeholder) else getString(R.string.value_in_m, minimumWorkingOutreach)
        workingOutreachValueTextView.horizontalBias = workingOutreachSlider.value.toFloat() / workingOutreachSlider.max

        locationSection.isVisible = canSelectLocation
        reachSection.isVisible = canSelectReach
        workingHeightGroup.isVisible = canSelectWorkingHeight
        workingOutreachGroup.isVisible = canSelectOutreach
        noResultsMessage.isVisible = numberOfResults == 0

        showResultsButton.isVisible = canShowResults
        showResultsButton.text = getString(R.string.help_me_choose_show_results_button_title, numberOfResults)

        if (isAnyOptionChanged) {
            scrollView.scrollToBottom()
        }
    }


    private fun OptionGroup(vararg options: HelpMeChooseOptionCard) = OptionGroup(options.toList())


    /*----------------------------------------- Classes ------------------------------------------*/


    private data class OptionGroup(val options: List<HelpMeChooseOptionCard>) {

        var selectedOption
            get() = options.firstOrNull { it.selection == HelpMeChooseOptionCard.Selection.SELECTED }
            set(value) {
                if (value == null) {
                    options.forEach { it.selection = HelpMeChooseOptionCard.Selection.NEUTRAL }
                } else {
                    value.selection = HelpMeChooseOptionCard.Selection.SELECTED
                    options.filter { it != value }.forEach { it.selection = HelpMeChooseOptionCard.Selection.NOT_SELECTED }
                }
            }

    }


}
