package com.riwal.rentalapp.common.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.freshchat.consumer.sdk.Freshchat
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.addSubview
import com.riwal.rentalapp.model.ChatManager.Companion.CONSERVATION_OPTIONS
import kotlinx.android.synthetic.main.button_chat.view.*

class ChatButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var numberOfUnreadMessages = 0
        set(value) {
            field = value
            updateUI()
        }

    init {
        addSubview(R.layout.button_chat)

        chatButton_button.setOnClickListener {
            Freshchat.showConversations(context, CONSERVATION_OPTIONS)
        }

        updateUI()
    }

    private fun updateUI() {
        chatButton_button.setImageResource(if (numberOfUnreadMessages == 0) R.drawable.ic_chat else R.drawable.ic_chat_bubble)
        chatButton_numberOfUnreadMessagesTextView.text = "$numberOfUnreadMessages"
    }

}