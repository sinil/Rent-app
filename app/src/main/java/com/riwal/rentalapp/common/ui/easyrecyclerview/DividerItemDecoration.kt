package com.riwal.rentalapp.common.ui.easyrecyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.riwal.rentalapp.common.extensions.android.backgroundColor
import com.riwal.rentalapp.common.extensions.android.fillRect

// https://www.bignerdranch.com/blog/a-view-divided-adding-dividers-to-your-recyclerview-with-itemdecoration/
class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val divider: Drawable?
    private val dividerHeight: Int?
    var dividerLeftInset = 0
    var dividerRightInset = 0

    init {
        val attrs = intArrayOf(android.R.attr.listDivider)
        val styledAttributes = context.obtainStyledAttributes(attrs)
        divider = styledAttributes.getDrawable(0)
        dividerHeight = divider?.intrinsicHeight
        styledAttributes.recycle()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        parent as EasyRecyclerView

        val position = parent.getChildAdapterPosition(view)
        val indexPath = parent.indexPathForPosition(position) ?: return // Don't add extra offsets for section headers
        val isLastItemInSection = indexPath.row == parent.numberOfRowsInSection(indexPath.section) - 1

        if (isLastItemInSection) {
            return
        }

        outRect.bottom = dividerHeight?:0
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        parent as EasyRecyclerView

        for (i in 0 until parent.childCount) {

            val child = parent.getChildAt(i)
            val rowContentRect = rowContentRectForItemView(child)

            // Only add a divider when the child has height
            if (child.height <= 1) {
                continue
            }

            val position = parent.getChildAdapterPosition(child)

            // We don't use an item decorator for section headers (which have no index path)
            val indexPath = parent.indexPathForPosition(position) ?: continue
            val isLastItemInSection = indexPath.row == parent.numberOfRowsInSection(indexPath.section) - 1

            if (isLastItemInSection) {
                continue
            }

            val dividerBackgroundRect = createRect(
                    top = rowContentRect.bottom,
                    bottom = rowContentRect.bottom + (dividerHeight?:0),
                    left = rowContentRect.left,
                    right = rowContentRect.right
            )

            val color = child.backgroundColor ?: Color.TRANSPARENT
            canvas.fillRect(dividerBackgroundRect, color)

            val bottomDividerRect = createRect(
                    top = rowContentRect.bottom,
                    bottom = rowContentRect.bottom + (dividerHeight?:0),
                    left = rowContentRect.left + dividerLeftInset,
                    right = rowContentRect.right + dividerRightInset
            )

            divider?.bounds = bottomDividerRect
            divider?.draw(canvas)
        }
    }

    private fun rowContentRectForItemView(itemView: View): Rect {
        val params = itemView.layoutParams as RecyclerView.LayoutParams
        return createRect(
                top = itemView.top - params.topMargin,
                bottom = itemView.bottom + params.bottomMargin,
                left = itemView.left - params.leftMargin,
                right = itemView.right + params.rightMargin
        )
    }

    private fun createRect(left: Int, top: Int, right: Int, bottom: Int): Rect {
        return Rect(left, top, right, bottom)
    }
}
