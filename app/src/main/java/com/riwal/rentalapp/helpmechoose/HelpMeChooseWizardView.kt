package com.riwal.rentalapp.helpmechoose

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView

interface HelpMeChooseWizardView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()

    interface DataSource {
        fun canSelectLocation(view: HelpMeChooseWizardView): Boolean
        fun canSelectReachType(view: HelpMeChooseWizardView): Boolean
        fun canSelectWorkingHeight(view: HelpMeChooseWizardView): Boolean
        fun canSelectWorkingOutreach(view: HelpMeChooseWizardView): Boolean
        fun canShowResults(view: HelpMeChooseWizardView): Boolean
        fun liftType(view: HelpMeChooseWizardView): LiftType
        fun location(view: HelpMeChooseWizardView): Location
        fun reachType(view: HelpMeChooseWizardView): ReachType
        fun minimumWorkingHeight(view: HelpMeChooseWizardView): Int
        fun minimumWorkingOutreach(view: HelpMeChooseWizardView): Int
        fun maximumWorkingHeight(view: HelpMeChooseWizardView): Int
        fun maximumWorkingOutreach(view: HelpMeChooseWizardView): Int
        fun numberOfResults(view: HelpMeChooseWizardView): Int
    }

    interface Delegate {
        fun onLiftTypeSelected(view: HelpMeChooseWizardView, liftType: LiftType)
        fun onLocationSelected(view: HelpMeChooseWizardView, location: Location)
        fun onReachTypeSelected(view: HelpMeChooseWizardView, reachType: ReachType)
        fun onMinimumWorkingHeightChanged(view: HelpMeChooseWizardView, newValue: Int)
        fun onMinimumWorkingOutreachChanged(view: HelpMeChooseWizardView, newValue: Int)
        fun onShowResultsSelected(view: HelpMeChooseWizardView)
    }

}
