package com.riwal.rentalapp.common.extensions.firebase

import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Single

fun FirebaseInstanceId.getTokenAsync() = Single.create<String> { emitter ->
    instanceId.addOnSuccessListener { emitter.onSuccess(it.token) }
    instanceId.addOnFailureListener { emitter.onError(it) }
}