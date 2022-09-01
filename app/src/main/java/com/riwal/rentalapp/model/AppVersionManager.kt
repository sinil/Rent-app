package com.riwal.rentalapp.model

import android.content.SharedPreferences
import com.riwal.rentalapp.BuildConfig
import com.riwal.rentalapp.common.extensions.android.get
import com.riwal.rentalapp.common.extensions.android.set
import com.riwal.rentalapp.common.extensions.core.doInBackground
import com.riwal.rentalapp.model.AppVersionManager.AppVersionStatus.*
import com.riwal.rentalapp.model.api.BackendClient

class AppVersionManager(
        private val backendClient: BackendClient,
        private val preferences: SharedPreferences,
        private val activeCountry: Country?
) {


    /*----------------------------------------- Properties ---------------------------------------*/


    companion object{
        var updateAppURL: String = ""
    }

    var delegate: Delegate? = null

    val installedAppVersion = SemanticVersion(BuildConfig.VERSION_NAME)
    private var latestAppVersion = SemanticVersion("0.0.0")
    private var latestFeatureVersion = 0

    private var latestDisplayedAppVersion: SemanticVersion?
        get() = preferences["latestDisplayedAppVersion"]
        set(value) {
            preferences["latestDisplayedAppVersion"] = value
        }

    private var lastDisplayedFeatureVersion: Int
        get() = preferences["lastDisplayedFeatureVersion"] ?: 0
        set(value) {
            preferences["lastDisplayedFeatureVersion"] = value
        }


    /*------------------------------------------ Methods -----------------------------------------*/


    fun verify() = doInBackground {
        try {
            val appInfo = backendClient.appInfo()
            latestAppVersion = appInfo.currentVersion
            latestFeatureVersion = appInfo.featureVersion
            val appVersionStatus = appVersionStatus(appInfo)
            delegate?.onAppVersionStatusUpdated(appVersionStatus, updateAppURL)
            if (appInfo.featureVersion > lastDisplayedFeatureVersion
                    && (appVersionStatus == UPDATE_AVAILABLE_BUT_ALREADY_NOTIFIED || appVersionStatus == UP_TO_DATE)) {
                delegate?.isNewFeatureAvailable(appInfo.informationMessage)
            }
        } catch (error: Exception) {
            delegate?.onAppVersionStatusUpdateFailed(error)
        }
    }

    fun updateLatestDisplayedAppVersionCode() {
        latestDisplayedAppVersion = latestAppVersion
    }

    fun updateFeatureDialogHasBeenShowed() {
        lastDisplayedFeatureVersion = latestFeatureVersion
    }

    private fun appVersionStatus(appInfo: AppInfo): AppVersionStatus {

        val isAlreadyNotified = latestDisplayedAppVersion == appInfo.currentVersion

        return if (activeCountry?.name in appInfo.liveCountries) {
            updateAppURL = if (BuildConfig.COMPANY.toString() == "RIWAL") { appInfo.riwalAppLink } else { appInfo.manliftAppLink }
            MANDATORY_UPDATE_REQUIRED
        } else if (installedAppVersion < appInfo.minimumVersion) {
            MANDATORY_UPDATE_REQUIRED
        } else if (installedAppVersion < appInfo.currentVersion) {
            if (isAlreadyNotified) UPDATE_AVAILABLE_BUT_ALREADY_NOTIFIED else UPDATE_AVAILABLE
        } else {
            UP_TO_DATE
        }


//        return when {
//            (installedAppVersion < appInfo.minimumVersion)  -> MANDATORY_UPDATE_REQUIRED
//            installedAppVersion < appInfo.currentVersion -> if (isAlreadyNotified) UPDATE_AVAILABLE_BUT_ALREADY_NOTIFIED else UPDATE_AVAILABLE
//            else -> UP_TO_DATE
//        }
    }


    /*----------------------------------------- Interfaces ---------------------------------------*/


    interface Delegate {
        fun onAppVersionStatusUpdated(appVersionStatus: AppVersionStatus, updateAppURL: String)
        fun onAppVersionStatusUpdateFailed(error: Exception)
        fun isNewFeatureAvailable(informationMessage: String)
    }


    /*------------------------------------------- Enums ------------------------------------------*/


    enum class AppVersionStatus {
        UP_TO_DATE,
        UPDATE_AVAILABLE,
        UPDATE_AVAILABLE_BUT_ALREADY_NOTIFIED,
        MANDATORY_UPDATE_REQUIRED
    }
}