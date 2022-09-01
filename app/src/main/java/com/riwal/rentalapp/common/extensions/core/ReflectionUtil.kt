package com.riwal.rentalapp.common.extensions.core

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun valueForProperty(rootObject: Any, path: String): Any? {

    val pathComponents = path.split("/")

    if (pathComponents.isEmpty()) {
        return rootObject
    }

    val property = rootObject::class.memberProperties.find { it.name == pathComponents[0] } as? KProperty1<Any, Any?> ?: return null
    property.isAccessible = true
    val value = property.get(rootObject) ?: return null

    return when {
        pathComponents.count() == 1 -> value
        (value as? Map<String, Any> != null) -> valueForProperty(value[pathComponents[1]] ?: return null, pathComponents.drop(2).joinToString("/"))
        (value as? List<Any> != null) -> valueForProperty(value.getOrNull(pathComponents[1].toIntOrNull() ?: return null) ?: return null, pathComponents.drop(2).joinToString("/"))
        else -> valueForProperty(rootObject = value, path = pathComponents.drop(1).joinToString("/"))
    }

}