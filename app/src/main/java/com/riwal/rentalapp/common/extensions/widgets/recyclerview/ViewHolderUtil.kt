package com.riwal.rentalapp.common.extensions.widgets.recyclerview

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.riwal.rentalapp.common.extensions.android.dp

val RecyclerView.ViewHolder.resources
    get() = itemView.resources!!

val RecyclerView.ViewHolder.context
    get() = itemView.context!!

fun RecyclerView.ViewHolder.getString(@StringRes resId: Int?, vararg parameters: Any) = if (resId == null) null else resources.getString(resId, *parameters)

fun RecyclerView.ViewHolder.dp(value: Int) = resources.dp(value)