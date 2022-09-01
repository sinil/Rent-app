package com.riwal.rentalapp.depot

import com.riwal.rentalapp.model.Depot

class DepotController(val view: DepotView, val depots: List<Depot>) : DepotView.DataSource, DepotView.Delegate {


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this
    }


    /*----------------------------------- DepotView.DataSource -----------------------------------*/


    override fun depots(view: DepotView) = depots


    /*------------------------------------ DepotView.Delegate ------------------------------------*/


    override fun onNavigateBackSelected() {
        view.navigateBack()
    }
}