package com.riwal.rentalapp.common.extensions.firebase

import com.google.firebase.auth.FirebaseAuth
import com.riwal.rentalapp.common.extensions.rxjava.toSingle
import io.reactivex.Single

fun FirebaseAuth.rxSignInAnonymously() = if (currentUser == null) signInAnonymously().toSingle().map { currentUser!! } else Single.just(currentUser)