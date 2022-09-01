package com.riwal.rentalapp.common.extensions.datetime

import org.joda.time.DateTime

val DISTANT_PAST = DateTime.parse("0000-01-01T00:00:00")!!
val DISTANT_PAST_IN_SQL = DateTime.parse("1753-01-01T12:00:00")!!
val DISTANT_FUTURE = DateTime.parse("9999-12-31T23:59:59")!!
