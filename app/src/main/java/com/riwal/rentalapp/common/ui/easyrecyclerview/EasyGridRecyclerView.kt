package com.riwal.rentalapp.common.ui.easyrecyclerview

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.marginBottom
import kotlin.math.roundToInt

class EasyGridRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : EasyRecyclerView(context, attrs, defStyleAttr) {

    init {
        val defaultLayoutManager = UniformGridLayoutManager(this)
        val defaultSpacingItemDecoration = DefaultSpacingItemDecoration(interItemSpacing = 0)

        if (attrs != null) {
            val styledAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.EasyGridRecyclerView, 0, 0)
            defaultLayoutManager.numberOfColumns = styledAttributes.getInt(R.styleable.EasyGridRecyclerView_numberOfColumns, defaultLayoutManager.numberOfColumns)
            defaultSpacingItemDecoration.interItemSpacing = styledAttributes.getDimensionPixelOffset(R.styleable.EasyGridRecyclerView_interItemSpacing, 0)
            styledAttributes.recycle()
        }

        layoutManager = defaultLayoutManager
        addItemDecoration(defaultSpacingItemDecoration)
    }

    class UniformGridLayoutManager(val recyclerView: EasyGridRecyclerView) : GridLayoutManager(recyclerView.context, 3), LayoutManager {

        var numberOfColumns
            get() = spanCount
            set(value) {
                spanCount = value
            }

        init {
            // Section headers span the whole view width
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) = if (recyclerView.isSectionHeader(position)) numberOfColumns else 1
            }
        }

        override fun isAutoMeasureEnabled() = true

        override fun numberOfGridColumnsInSection(section: Int) = numberOfColumns

        override fun numberOfGridRowsInSection(section: Int) = (recyclerView.numberOfRowsInSection(section) - 1) / numberOfColumns + 1
    }

    class DefaultSpacingItemDecoration(var interItemSpacing: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(insets: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(insets, view, parent, state)

            if (parent !is EasyRecyclerView) {
                return
            }

            val layoutManager = parent.layoutManager as UniformGridLayoutManager

            val position = parent.getChildAdapterPosition(view)
            val isSectionHeader = parent.isSectionHeader(position)

            if (isSectionHeader) {
                return // Inter-item spacing does not apply to section headers
            }

            val numberOfColumns = layoutManager.numberOfColumns
            val totalInset = interItemSpacing * (numberOfColumns - 1)
            val insetPerItem = totalInset.toFloat() / numberOfColumns.toFloat()

            val indexPath = parent.indexPathForPosition(position)!!
            val column = layoutManager.gridColumnForIndexPath(indexPath)
            val row = layoutManager.gridRowForIndexPath(indexPath)
            val startInsetStep = insetPerItem / (numberOfColumns - 1)

            insets.set(
                    (column * startInsetStep).roundToInt(),
                    (row * startInsetStep).roundToInt(),
                    (insetPerItem - column * startInsetStep).roundToInt(),
                    (insetPerItem - row * startInsetStep).roundToInt()
            )

            if(layoutManager.isInLastRow(indexPath))
                insets.bottom = 20
        }
    }

    interface LayoutManager {

        fun numberOfGridColumnsInSection(section: Int): Int

        fun numberOfGridRowsInSection(section: Int): Int

        fun isInFirstColumn(indexPath: IndexPath) = gridColumnForIndexPath(indexPath) == 0

        fun isInLastColumn(indexPath: IndexPath) = gridColumnForIndexPath(indexPath) == numberOfGridColumnsInSection(indexPath.section) - 1

        fun gridColumnForIndexPath(indexPath: IndexPath) = indexPath.row % numberOfGridColumnsInSection(indexPath.section)

        fun isInFirstRow(indexPath: IndexPath) = gridRowForIndexPath(indexPath) == 0

        fun isInLastRow(indexPath: IndexPath) = gridRowForIndexPath(indexPath) == numberOfGridRowsInSection(indexPath.section) - 1

        fun gridRowForIndexPath(indexPath: IndexPath) = indexPath.row / numberOfGridColumnsInSection(indexPath.section)

    }
}
