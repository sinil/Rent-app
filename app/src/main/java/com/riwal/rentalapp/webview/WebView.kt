package com.riwal.rentalapp.webview

interface WebView {

    var dataSource: DataSource

    interface DataSource {
        fun pageTitle(view: WebView): String
        fun pageUrl(view: WebView): String
    }
}