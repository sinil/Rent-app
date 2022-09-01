package com.riwal.rentalapp.main.account

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.addSubview
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.model.AccountManager
import com.riwal.rentalapp.model.Customer
import kotlinx.android.synthetic.main.view_company.view.*

class CustomerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {


    /*--------------------------------------- Properties -----------------------------------------*/

    var isCustomerNameVisible = false

    var customer: Customer? = null
        set(value) {
            field = value
            customerNameTextView.isVisible = isCustomerNameVisible
            customerNameTextView.text = value?.name
        }

    var accountManager: AccountManager? = null
        set(value) {
            field = value

            val isUseful = value?.isUseful == true
            accountManagerTextView.isVisible = isUseful
            accountManagerValueTextView.isVisible = isUseful
            accountManagerValueTextView.text = value?.name

            phoneNumberTextView.isVisible = !value?.phoneNumber.isNullOrBlank()
            phoneNumberTextView.text = value?.phoneNumber
            phoneNumberTextView.setOnClickListener {
                val phoneNumber = value?.phoneNumber
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                context.startActivity(intent)
            }

            emailTextView.isVisible = !value?.email.isNullOrBlank()
            emailTextView.text = value?.email
            emailTextView.setOnClickListener {
                val email = value?.email
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                context.startActivity(Intent.createChooser(intent, null))
            }
        }

    var accountManagerPicture: Drawable? = null
        set(value) {
            field = value
            if (value == null){
                try {
                    photoImageView.setImageResource(R.drawable.ic_avatar)
                } catch (e: Exception) {
                }
            } else{
                try {
                    photoImageView.setImageDrawable(value)
                } catch (e: Exception) {
                }
            }

        }

    var isLoading = false
        set(value) {
            field = value
            accountManagerProgressBar.isVisible = value
            accountManagerView.isVisible = !value
        }


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        addSubview(R.layout.view_company)
    }


}
