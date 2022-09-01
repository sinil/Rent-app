package com.riwal.rentalapp.machinedetail.ar

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView

interface ArHelpView : MvcView, ObservableLifecycleView {

    var delegate: Delegate

    interface Delegate {
        fun onDismissSelected(view: ArHelpView)
    }

}