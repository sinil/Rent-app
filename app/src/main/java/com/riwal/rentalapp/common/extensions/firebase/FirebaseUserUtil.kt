package com.riwal.rentalapp.common.extensions.firebase

import com.google.firebase.auth.FirebaseUser
import com.riwal.rentalapp.common.extensions.rxjava.toSingle
import io.reactivex.schedulers.Schedulers

fun FirebaseUser.rxIdToken(forceRefresh: Boolean) = getIdToken(forceRefresh)
        .toSingle()
        .subscribeOn(Schedulers.io())
        .map { it.token!! }