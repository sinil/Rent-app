package com.riwal.rentalapp

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import io.mockk.mockk

class FakePreferences : SharedPreferences by mockk() {


    /*---------------------------------------- Properties ----------------------------------------*/


    private var backingMap: MutableMap<String, Any> = mutableMapOf()
    private val editor = Editor()


    /*------------------------------------- SharedPreferences ------------------------------------*/


    override fun edit() = editor
    override fun contains(key: String) = backingMap.contains(key)

    override fun getAll() = backingMap
    override fun getStringSet(key: String?, defValues: MutableSet<String>?) = (backingMap[key] as? MutableSet<String>) ?: defValues
    override fun getBoolean(key: String, defValue: Boolean) = (backingMap[key] as? Boolean) ?: defValue
    override fun getString(key: String?, defValue: String?) = (backingMap[key] as? String) ?: defValue
    override fun getFloat(key: String?, defValue: Float) = (backingMap[key] as? Float) ?: defValue
    override fun getLong(key: String?, defValue: Long) = (backingMap[key] as? Long) ?: defValue
    override fun getInt(key: String, defValue: Int) = (backingMap[key] as? Int) ?: defValue

    override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    /*------------------------------------------ Classes -----------------------------------------*/


    inner class Editor : SharedPreferences.Editor {

        override fun clear(): SharedPreferences.Editor {
            backingMap = mutableMapOf()
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            backingMap[key] = value
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            backingMap[key] = value
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor {
            backingMap.remove(key)
            return this
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            backingMap[key] = value
            return this
        }

        override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor? {
//            backingMap[key] = values
            return this
        }

        override fun commit() = true

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            backingMap[key] = value
            return this
        }

        override fun apply() {
            // Does nothing
        }

        override fun putString(key: String?, value: String?): SharedPreferences.Editor? {
//            backingMap[key] = value
            return this
        }
    }
}