package com.riwal.rentalapp.model

import com.riwal.rentalapp.common.extensions.core.StringCompareOptions.NUMERIC
import com.riwal.rentalapp.common.extensions.core.compareTo

data class SemanticVersion(val versionString: String) : Comparable<SemanticVersion> {

    override fun compareTo(other: SemanticVersion) = versionString.compareTo(other.versionString, options = setOf(NUMERIC))

    override fun toString() = versionString

}

fun String.toSemanticVersion() = SemanticVersion(this)
