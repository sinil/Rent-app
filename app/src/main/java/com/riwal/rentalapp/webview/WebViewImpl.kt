package com.riwal.rentalapp.webview

import android.annotation.SuppressLint
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings.LOAD_NO_CACHE
import android.webkit.WebViewClient
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.webview.WebView.DataSource
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_web.*


class WebViewImpl : RentalAppNotificationActivity(), WebView {


    /*--------------------------------------- Properties -----------------------------------------*/


    override lateinit var dataSource: DataSource

    private val pageTitle
        get() = dataSource.pageTitle(view = this)

    private val pageUrl
        get() = dataSource.pageUrl(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_web)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = pageTitle

        webView.settings.cacheMode = LOAD_NO_CACHE
        webView.settings.setAppCacheEnabled(false)
        webView.clearCache(true)
        webView.clearFormData()
        webView.clearHistory()
        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: android.webkit.WebView?, request: WebResourceRequest?): Boolean {
                activityIndicator.isVisible = true
                view?.loadUrl(request?.url.toString())
                return true
            }

            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                super.onPageFinished(view, url)
                activityIndicator.isVisible = false
            }
        }

        webView.loadUrl(pageUrl)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> navigateBack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        navigateBack()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun navigateBack() = finish()

}