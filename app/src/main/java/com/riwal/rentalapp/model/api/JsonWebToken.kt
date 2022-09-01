package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.common.extensions.core.decodeBase64
import com.riwal.rentalapp.common.extensions.json.fromJson
import org.joda.time.DateTime
import org.joda.time.DateTime.now

data class JsonWebToken(val string: String, val header: Header, val payload: Payload, val signature: String, val payloadString: String) {

    data class Header(val alg: String, val typ: String)
    data class Payload(val iat: Long, val exp: Long)

    val issuedAt = DateTime(payload.iat * 1000) // iat is an epoch time in seconds
    val expires = DateTime(payload.exp * 1000) // exp is an epoch time in seconds
}

fun JsonWebToken(string: String?): JsonWebToken? {

    if (string == null) return null

    val elements = string.split(".")
    if (elements.size < 3) return null

    val payloadString = elements[1].decodeBase64()

    return JsonWebToken(
            string = string,
            header = fromJson(elements[0].decodeBase64()) ?: return null,
            payload = fromJson(payloadString) ?: return null,
            signature = elements[2],
            payloadString = payloadString
    )
}

val JsonWebToken.isExpired
    get() = now() >= expires