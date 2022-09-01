package com.riwal.rentalapp.requestchangesform

import com.riwal.rentalapp.common.BetterBundle
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.AccessoriesOnRent
import com.riwal.rentalapp.model.Place
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.model.RentalStatus.*
import com.riwal.rentalapp.placepicker.PlacePickerController

class ChangeRequestFormController(val view: ChangeRequestFormView, val firstDayOfWeek: Int) : ViewLifecycleObserver, ChangeRequestFormView.DataSource, ChangeRequestFormView.Delegate, PlacePickerController.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    var delegate: Delegate? = null
    private var originalRental: Rental? = null

    private  var changedRental: Rental? = null

    private var originalAccessories: AccessoriesOnRent? = null
    private var changedAccessories: AccessoriesOnRent? = null


    private val canChangeOnRentDateTime
        get() = if(originalRental != null) originalRental?.status in listOf(UNKNOWN, PENDING_DELIVERY) else originalAccessories?.status in listOf(UNKNOWN, PENDING_DELIVERY)


    private val canChangeOffRentDateTime
        get() = if (originalRental != null) originalRental!!.isOffRentDateFinal && originalRental?.status in listOf(UNKNOWN, PENDING_DELIVERY, ON_RENT) else originalAccessories!!.isOffRentDateFinal && originalAccessories?.status in listOf(UNKNOWN, PENDING_DELIVERY, ON_RENT)

    private val canSubmitChanges
        get() = if (originalRental != null) changedRental != originalRental else changedAccessories != originalAccessories


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewAppear() {
        super.onViewAppear()
        view.notifyDataChanged()
    }

    override fun onViewSave(state: BetterBundle) {
        super.onViewSave(state)
        state["originalRental"] = originalRental
        state["changedRental"] = changedRental

        state["originalRental"] = originalAccessories
        state["changedRental"] = changedAccessories
    }

    override fun onViewRestore(savedState: BetterBundle) {
        super.onViewRestore(savedState)

        originalRental = savedState["originalRental"]!!
        changedRental = savedState["changedRental"]!!

        originalAccessories = savedState["originalRental"]!!
        changedAccessories = savedState["changedRental"]!!

        if (delegate == null) {
            view.navigateBack()
        }
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    fun setOriginalRental(rental: Rental) {
        originalRental = rental
        changedRental = rental.copy()
    }

    fun setOriginalRental(accessories: AccessoriesOnRent) {
        originalAccessories = accessories
        changedAccessories = accessories.copy()
    }


    /*----------------------------- ChangeRequestFormView DataSource -----------------------------*/


    override fun rental(view: ChangeRequestFormView) = changedRental
    override fun accessories(view: ChangeRequestFormView) = changedAccessories
    override fun canChangeOnRentDateTime(view: ChangeRequestFormView) = canChangeOnRentDateTime
    override fun canChangeOffRentDateTime(view: ChangeRequestFormView) = canChangeOffRentDateTime
    override fun canSubmit(view: ChangeRequestFormView) = canSubmitChanges
    override fun firstDayOfWeek(view: ChangeRequestFormView) = firstDayOfWeek


    /*------------------------------ ChangeRequestFormView Delegate ------------------------------*/


    override fun onRentalChanged(view: ChangeRequestFormView, changedRental: Rental) {
        this.changedRental = changedRental
        view.notifyDataChanged()
    }

    override fun onAccessoriesChanged(view: ChangeRequestFormView, changedAccessories: AccessoriesOnRent) {
        this.changedAccessories = changedAccessories
        view.notifyDataChanged()
    }

    override fun onPickPlaceSelected(view: ChangeRequestFormView) {
        view.navigateToPlacePickerPage { controller ->
            controller as PlacePickerController
            controller.delegate = this
        }
    }

    override fun onSubmitChangesSelected(view: ChangeRequestFormView) {
        view.askForComments { comments ->
            if (originalRental != null) {
                delegate?.onRentalChanged(controller = this, originalRental = originalRental!!, changedRental = changedRental!!, comments = comments)
            }else {
                delegate?.onAccessoriesChanged(controller = this, originalAccessories = originalAccessories!!, changedAccessories = changedAccessories!!, comments = comments)
            }
            view.navigateBack()
        }
    }

    override fun onBackPressed(view: ChangeRequestFormView) {
        view.navigateBack()
    }


    /*------------------------------ PlacePickerController Delegate ------------------------------*/


    override fun onPlacePicked(controller: PlacePickerController, place: Place) {

        if (changedRental != null) {
            this.changedRental = changedRental!!.copy(
                    project = changedRental!!.project.copy(
                            address = place.address,
                            coordinate = place.coordinate
                    )
            )
        } else {
            this.changedAccessories = changedAccessories!!.copy(
                    project = changedAccessories!!.project.copy(
                            address = place.address,
                            coordinate = place.coordinate
                    )
            )
        }

        view.notifyDataChanged()
    }


    /*----------------------------------------- Interfaces ---------------------------------------*/


    interface Delegate {
        fun onRentalChanged(controller: ChangeRequestFormController, originalRental: Rental, changedRental: Rental, comments: String)
        fun onAccessoriesChanged(controller: ChangeRequestFormController, originalAccessories: AccessoriesOnRent, changedAccessories: AccessoriesOnRent, comments: String)
    }

}