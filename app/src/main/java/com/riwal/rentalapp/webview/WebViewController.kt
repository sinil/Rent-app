package com.riwal.rentalapp.webview

class WebViewController(val view: WebView) : WebView.DataSource {


    /*--------------------------------------- Properties -----------------------------------------*/


    lateinit var pageTitle: String
    lateinit var pageUrl: String


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {

        view.dataSource = this

    }


    /*------------------------------------ WebView DataSource ------------------------------------*/


    override fun pageTitle(view: WebView) = pageTitle
    override fun pageUrl(view: WebView) = pageUrl

}