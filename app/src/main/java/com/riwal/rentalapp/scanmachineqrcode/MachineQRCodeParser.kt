package com.riwal.rentalapp.scanmachineqrcode

object MachineQRCodeParser {

    // Format of QR code contents: https://documentation.riwal.com/{fleet number}
    fun fleetNumber(contents: String): String?{
        if (!contents.startsWith("https://documentation.riwal.com/")) {
            return null
        }

        // pathComponents contains all path components after the base URL. For example, for the URL
        // https://test.com/bla/meh?limit=1 pathComponents contains ["/", "bla", "meh"]. The first
        // component for a valid URL is always "/". For the URLs encoded in the machine QR codes,
        // the fleet number is in the path component directly after the first "/", so at index 1.
        return contents.split("com/")[1]
    }
}