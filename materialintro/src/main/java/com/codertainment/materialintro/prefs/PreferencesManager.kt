package com.codertainment.materialintro.prefs

import android.content.Context
import com.codertainment.materialintro.utils.SingletonHolder

internal class PreferencesManager private constructor(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun isDisplayed(id: String?): Boolean {
        return if (isTutorialSkipped()) true else sharedPreferences.getBoolean(id, false)
    }

    fun setDisplayed(id: String?) {
        sharedPreferences.edit().putBoolean(id, true).apply()
    }

    fun reset(id: String?) {
        sharedPreferences.edit().putBoolean(id, false).apply()
    }

    fun resetAll() {
        sharedPreferences.edit().clear().apply()
    }

    private fun isTutorialSkipped(): Boolean {
        return sharedPreferences.getBoolean(IS_SKIPPED, false)
    }

    fun skipTutorial() {
        sharedPreferences.edit().putBoolean(IS_SKIPPED, true).apply()
    }

    companion object : SingletonHolder<PreferencesManager, Context>(::PreferencesManager) {
        private const val PREFERENCES_NAME = "material_intro_preferences"
        private const val IS_SKIPPED = "is_tutorial_skipped"
    }
}