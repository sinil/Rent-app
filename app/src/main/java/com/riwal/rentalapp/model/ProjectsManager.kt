package com.riwal.rentalapp.model

import android.content.SharedPreferences
import com.riwal.rentalapp.common.extensions.json.fromListJson
import com.riwal.rentalapp.common.extensions.android.get
import com.riwal.rentalapp.common.extensions.core.replaceFirstWhere
import com.riwal.rentalapp.common.extensions.android.set

class ProjectsManager(val preferences: SharedPreferences) {


    /*--------------------------------------- Properties -----------------------------------------*/


    var projects: List<Project> = fromListJson(preferences["projects"]) ?: emptyList()
        private set(value) {
            field = value
            save()
        }


    /*----------------------------------------- Methods ------------------------------------------*/


    fun put(project: Project) {
        projects = projects.replaceFirstWhere({ it.id == project.id }, new = project)
    }

    fun remove(project: Project) {
        projects -= projects.first { it.id == project.id }
    }


    /*-------------------------------------- Private methods -------------------------------------*/


    private fun save() {
        preferences["projects"] = projects
    }

}
