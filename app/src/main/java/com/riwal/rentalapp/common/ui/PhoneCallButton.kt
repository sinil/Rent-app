package com.riwal.rentalapp.common.ui

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.FrameLayout
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.addSubview
import com.riwal.rentalapp.common.extensions.core.toUri
import kotlinx.android.synthetic.main.button_phone_call.view.*

class PhoneCallButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var phoneNumber = ""

    init {
        addSubview(R.layout.button_phone_call)

        phoneCall_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri())
            context.startActivity(intent)
        }

    }


}