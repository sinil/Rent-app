package com.riwal.rentalapp.model

import android.util.Log
import com.freshchat.consumer.sdk.ConversationOptions
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatCallbackStatus.STATUS_SUCCESS
import com.riwal.rentalapp.common.extensions.core.doInBackground
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import kotlinx.coroutines.rx2.await
import java.util.concurrent.TimeUnit.SECONDS

class ChatManager(val freshchat: Freshchat, val countryManager: CountryManager) {


    /*--------------------------------------- Properties -----------------------------------------*/


    val isChatEnabled
        get() = countryManager.activeCountry?.isChatEnabled ?: false

    var numberOfUnreadMessages = 0

    val observableNumberOfUnreadMessages: Observable<Int> = Observable.create { emitter: ObservableEmitter<Int> ->
        doInBackground {
            try {
                numberOfUnreadMessages = numberOfUnreadMessages(CONVERSATION_TAGS).await()
                Log.d("ChatManager", "Unread message count: $numberOfUnreadMessages")
                emitter.onNext(numberOfUnreadMessages)
                emitter.onComplete()
            } catch (error: Exception) {
                error.printStackTrace()
                emitter.onNext(0)
            }
        }
    }.repeatWhen { observable -> observable.delay(2, SECONDS) }.distinctUntilChanged()


    /*------------------------------------ Private methods ---------------------------------------*/


    private fun numberOfUnreadMessages(conversationTags: List<String>) = Single.create { emitter: SingleEmitter<Int> ->
        freshchat.getUnreadCountAsync({ freshchatCallbackStatus, unreadCount ->
            if (freshchatCallbackStatus == STATUS_SUCCESS) {
                emitter.onSuccess(unreadCount)
            } else {
                emitter.onError(Throwable("Unable to get the unread message count"))
            }
        }, conversationTags)
    }


    /*--------------------------------------- Constants ------------------------------------------*/

    
    companion object {
        val CONVERSATION_TAGS = listOf("rentalOrder")
        val CONSERVATION_OPTIONS = ConversationOptions().filterByTags(CONVERSATION_TAGS, "RentalOrder")
    }

}