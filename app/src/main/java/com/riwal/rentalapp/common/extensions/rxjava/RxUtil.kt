package com.riwal.rentalapp.common.extensions.rxjava

import com.google.android.gms.tasks.Task
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.joda.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

fun <T> Task<T>.toSingle() = Single.create<T> { emitter: SingleEmitter<T> ->
    this.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            emitter.onSuccess(task.result!!)
        } else {
            emitter.onError(task.exception!!)
        }
    }
}

fun Disposable.disposedBy(compositeDisposable: CompositeDisposable): Disposable {
    compositeDisposable += this
    return this
}

fun <T> Observable<T>.debounce(timeout: Duration) = debounce(timeout.millis, MILLISECONDS)

fun <T> Observable<T>.throttleLatest(timeout: Duration) = throttleLatest(timeout.millis, TimeUnit.MILLISECONDS)