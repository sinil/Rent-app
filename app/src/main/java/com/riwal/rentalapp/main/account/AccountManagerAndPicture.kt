package com.riwal.rentalapp.main.account

import android.graphics.drawable.Drawable
import com.riwal.rentalapp.model.AccountManager


data class AccountManagerAndPicture(var accountManager: AccountManager? = null, var picture: Drawable? = null, var isUpdating: Boolean = false)