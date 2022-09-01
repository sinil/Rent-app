package com.riwal.rentalapp.common.extensions.widgets

import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.navigationItemAt(position: Int): BottomNavigationItemView {
    val menuView = getChildAt(0) as BottomNavigationMenuView
    return menuView.getChildAt(position) as BottomNavigationItemView
}