package com.riwal.rentalapp.common.extensions.glide

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.riwal.rentalapp.GlideApp
import com.riwal.rentalapp.GlideRequest
import com.riwal.rentalapp.GlideRequests
import io.reactivex.Single
import io.reactivex.SingleEmitter


fun GlideRequests.loadImage(url: GlideUrl) = Single.create { emitter: SingleEmitter<Drawable> ->
    load(url).addRequestListener({ drawable ->
        if (!emitter.isDisposed) {
            emitter.onSuccess(drawable)
        }
    }, { error ->
        if (!emitter.isDisposed) {
            emitter.onError(error)
        }
    }).submit()
}

@SuppressLint("CheckResult")
fun GlideRequest<Drawable>.addRequestListener(onResourceReady: (resource: Drawable) -> Unit, onLoadFailed: (error: GlideException) -> Unit): GlideRequest<Drawable> {
    listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            onLoadFailed(e ?: GlideException("Loading the image failed."))
            return true
        }

        override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            onResourceReady(resource)
            return true
        }
    })
    return this
}

fun ImageView.loadImage(url: String?, onComplete: (drawable: Drawable?) -> Unit) {
    GlideApp.with(context).load(url).listener(object : RequestListener<Drawable> {

        override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            onComplete(resource)
            return false
        }

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            onComplete(null)
            return false
        }
    }).into(this)
}

fun ImageView.loadImage(url: String?) {
    GlideApp.with(context).load(url).into(this)
}

fun ImageView.loadImage(url: String?, placeholder: Int) {
    GlideApp.with(context).load(url).placeholder(placeholder).error(placeholder).into(this)
}

fun ImageView.loadImage(url: String?, token: String) {
    val headers = LazyHeaders.Builder().addHeader("Authorization", "Bearer $token").build()
    val glideUrl = GlideUrl(url, headers)
    GlideApp.with(context).load(glideUrl).into(this)
}

fun ImageView.loadImage(resourceId: Int) {
    GlideApp.with(context).load(resourceId).into(this)
}