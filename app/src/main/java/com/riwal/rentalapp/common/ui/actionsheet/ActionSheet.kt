package com.riwal.rentalapp.common.ui.actionsheet

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.transition.Slide
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.animation.transitions
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.common.ui.easyrecyclerview.MaterialRowViewHolder
import com.riwal.rentalapp.common.ui.easyrecyclerview.MaterialRowViewHolder.ImageStyle.*
import kotlinx.android.synthetic.main.action_sheet.view.*

class ActionSheet @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    var actions: List<Action> = emptyList()

    var isOpen = false
        private set

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {

        addSubview(R.layout.action_sheet)

        actionSheetOverlay.setOnClickListener { close() }

        recyclerView.dataSource = this
        recyclerView.delegate = this

    }


    /*------------------------------- EasyRecyclerView DataSource --------------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = actions.size


    /*--------------------------------- EasyRecyclerView Delegate --------------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        viewHolder as MaterialRowViewHolder

        val action = actions[indexPath.row]
        viewHolder.title = action.title
        viewHolder.setImage(action.iconRes, SQUARE_SMALL)

    }

    override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val action = actions[indexPath.row]
        action.handler?.invoke()
    }


    /*---------------------------------------- Methods -------------------------------------------*/


    fun show(parentView: ViewGroup) {
        parentView.addView(this)
        isOpen = true
        updateUI()
    }

    fun close() {
        isOpen = false
        updateUI()
    }


    /*---------------------------------------- Methods -------------------------------------------*/


    fun updateUI(animated: Boolean = true) = runOnUiThread {

        if (animated) {

            val transitionSet = ParallelAutoTransition()
            transitionSet.transitions.forEach { it?.excludeTarget(bottomSheet, true) }
            transitionSet.addTransition(Slide().addTarget(bottomSheet))

            if (!isOpen) {
                transitionSet.onTransitionEnd { removeFromParent() }
            }

            activity.beginDelayedTransition(transitionSet)

        }

        actionSheetOverlay.isVisible = isOpen
        bottomSheet.isVisible = isOpen

    }


    /*---------------------------------- Interfaces / Classes ------------------------------------*/


    data class Action(@DrawableRes val iconRes: Int, val title: String, val handler: (() -> Unit)? = null)


}