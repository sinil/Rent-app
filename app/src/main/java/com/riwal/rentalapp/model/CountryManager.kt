package com.riwal.rentalapp.model

import android.content.SharedPreferences
import com.riwal.rentalapp.common.extensions.core.tryValueOf
import com.riwal.rentalapp.common.extensions.android.get
import com.riwal.rentalapp.common.extensions.android.set
import java.util.*

class CountryManager(val locale: Locale, val preferences: SharedPreferences) {


    /*--------------------------------------- Properties -----------------------------------------*/


    private var observers: List<Observer> = emptyList()

    val countries
        get() = Country.countriesForCurrentBrand

    var activeCountry = preferences.activeCountry ?: countryForLocale ?: Country.defaultForCurrentBrand
        set(value) {
            field = value
            preferences.activeCountry = value
            notifyObservers()
        }

    private val countryForLocale: Country?
        get() {
            val country = tryValueOf<Country>(locale.country)
            return if (country in Country.countriesForCurrentBrand) country else null
        }

    val isCountrySelected
        get() = preferences.activeCountry != null


    /*---------------------------------------- Methods -------------------------------------------*/


    fun addObserver(observer: Observer) {
        observers += observer
        observer.onCountryChanged(countryManager = this, country = activeCountry!!)
    }

    fun removeObserver(observer: Observer) {
        observers -= observer
    }


    /*------------------------------------ Private methods ---------------------------------------*/


    private fun notifyObservers() {
        observers.forEach {
            it.onCountryChanged(countryManager = this, country = activeCountry!!)
        }
    }


    /*--------------------------------------- Interfaces -----------------------------------------*/


    interface Observer {
        fun onCountryChanged(countryManager: CountryManager, country: Country)
    }
}

var SharedPreferences.activeCountry
    get() = tryValueOf<Country>(this["selectedCountryCode"])
    set(value) {
        this["selectedCountryCode"] = value?.name
    }