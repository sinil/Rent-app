package com.riwal.rentalapp.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.riwal.rentalapp.BuildConfig.FLAVOR_brand
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.datetime.toLocalTime
import com.riwal.rentalapp.model.DayOfWeek.*
import org.joda.time.LocalTime
import java.util.*

enum class DayOfWeek(val value: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    companion object {
        fun fromValue(value: Int) = DayOfWeek.values().find { it.value == value }!!
    }
}


enum class Country(
        @DrawableRes val picture: Int,
        @StringRes val localizedNameRes: Int,
        val nextDayDeliveryCutoffTime: LocalTime,
        val sameDayOffRentCutoffTime: LocalTime,
        val isChatEnabled: Boolean,
        val isAccess4UAvailable: Boolean,
        val rentalDeskContactInfo: List<ContactInfo>,
        val otherContactInfo: List<ContactInfo>,
        val depots: List<Depot>,
        val weekend: List<DayOfWeek>,
        val firstDayOfWeek: Int,
        val isBreakdownReportingEnabled: Boolean,
        val isPhoneCallEnable: Boolean,
        val isAddAccessoriesEnabled: Boolean,
        val isBookingTrainingEnabled: Boolean,
        val trainingFromURL: Boolean,
        val trainingURL: String = "",
        val gamingURL: String = "",
        val company: Int,

        val isGamingEnabled: Boolean,
        val isMyProjectsEnabled: Boolean,
        val isMyQuotationEnabled: Boolean) {

    AE(
            picture = R.drawable.flag_ae,
            localizedNameRes = R.string.country_uae,
            nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "15:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = false,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Rental", "+971 4 885 6065", "info@manliftgroup.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Sales", "+971 4 885 6065", "sales@manliftgroup.com")
            ),
            depots = listOf(
                    Depot(
                            name = "Manlift Middle East LLC (Regional Headquarters)",
                            address = "Dubai Investment Park 1, Sheikh Mohammed Bin Zayed Road PO Box: 213645 Dubai",
                            coordinate = Coordinate(25.000402, 55.153518),
                            phoneNumber = "+971 4 885 6065",
                            email = "info@manliftgroup.com"
                    ),
                    Depot(
                            name = "Manlift Middle East - Yard",
                            address = "Dubai Investment Park 2 PO Box: 213645 Dubai",
                            coordinate = Coordinate(24.985851, 55.209110),
                            phoneNumber = "+971 4 884 9553",
                            email = "info@manliftgroup.com"
                    )
            ),
            weekend = listOf(FRIDAY, SATURDAY),
            firstDayOfWeek = Calendar.SUNDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 56,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = false
    ),
    BE(
            picture = R.drawable.flag_be,
            localizedNameRes = R.string.country_belgium,
            nextDayDeliveryCutoffTime = "14:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "10:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = true,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Verhuur", "+32 (0)2 881 81 81", "info.be@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Verkoop", "+31 (0)78 618 18 88", "verkoop@riwal.com"),
                    ContactInfo("Onderdelen", "+31 (0)78 618 18 88", "parts@riwal.com"),
                    ContactInfo("Onderhoud", "+31 (0)78 618 18 88", "onderhoud@riwal.com"),
                    ContactInfo("Opleidingen", "+31 (0)78 620 68 68", "opleidingen@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Vestiging Antwerpen", address = "Jacobsveldweg 8, 2160 Wommelgem", phoneNumber = "+32 (0)2 881 81 81", email = "info.be@riwal.com"),
                    Depot(name = "Vestiging Genk", address = "Winterbeeklaan 31, 3600 Genk", phoneNumber = "+32 (0)2 881 81 81", email = "info.be@riwal.com"),
                    Depot(name = "Hoofdkantoor Riwal Benelux", address = "Maxwellstraat 27, 3316 GP Dordrecht", phoneNumber = "+31 (0)78 618 18 88", email = "info@riwal.com"),
                    Depot(name = "Vestiging Dordrecht, Riwal Nederland", address = "Maxwellstraat 27, 3316 GP Dordrecht", phoneNumber = "+31 (0)78 654 39 22", email = "verhuurdordrecht@riwal.com"),
                    Depot(name = "Vestiging Maasvlakte Rotterdam, Riwal Nederland", address = "Magallanesstraat 8, 3199 LP Maasvlakte Rotterdam", phoneNumber = "+31 (0)10 269 60 09", email = "maasvlakte@riwal.com"),
                    Depot(name = "Vestiging Eindhoven, Riwal Nederland", address = "Ekkersrijt 3111, 5692 CD Son en Breugel", phoneNumber = "+31 (0)40 261 96 49", email = "verhuureindhoven@riwal.com"),
                    Depot(name = "Vestiging Amsterdam, Riwal Nederland", address = "Muiderstraatweg 63A, 1111 PV Diemen", phoneNumber = "+31 (0)20 449 69 99", email = "verhuuramsterdam@riwal.com"),
                    Depot(name = "Vestiging Hengelo, Riwal Nederland", address = "Vosboerweg 7, 7556 BT Hengelo", phoneNumber = "+31 (0)74 201 09 70", email = "verhuurhengelo@riwal.com"),
                    Depot(name = "Vestiging Groningen, Riwal Nederland", address = "Potklei 6, 9351 VS Leek", phoneNumber = "+31 (0)50 201 17 00", email = "verhuurgroningen@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = false,
            trainingFromURL = false,
            company = 16,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = false
    ),
    CORPORATE(
            picture = R.drawable.flag_international,
            localizedNameRes = R.string.country_international,
            nextDayDeliveryCutoffTime = "07:30".toLocalTime()!!,
            sameDayOffRentCutoffTime = "07:30".toLocalTime()!!,
            isChatEnabled = true,
            isPhoneCallEnable = false,
            isAccess4UAvailable = true,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Rental", "+31 (0)88 618 18 18", "international_rental@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Sales", "+31 (0)88 618 18 00", "sales@riwal.com"),
                    ContactInfo("Maintenance", "+31 (0)78 654 37 89", "onderhoud@riwal.com"),
                    ContactInfo("Parts", "+31 (0)78 618 18 88", "parts@riwal.com"),
                    ContactInfo("Training", "+31 (0)78 620 68 68", "info@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Riwal Holding Group", address = "Wilgen bos 2, 3311 JX Dordrecht", phoneNumber = "+31 (0)88 618 18 00", email = "info@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = false,
            trainingFromURL = false,
            company = 56,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = true
    ),
    DE(
            picture = R.drawable.flag_de,
            localizedNameRes = R.string.country_germany,
            nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "15:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = false,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Mietanfragen", "+49 (0) 800 300 1100", "info-de@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Maschinenverkauf", "+49 (0) 800 300 1100", "verkauf@riwal.com"),
                    ContactInfo("Wartungsverträge", "+49 (0) 800 300 1100", "technik@riwal.com"),
                    ContactInfo("Reparaturservice", "+49 (0) 800 300 1100", "technik@riwal.com"),
                    ContactInfo("Servicehotline", "+49 (0) 800 300 1100", "info-de@riwal.com"),
                    ContactInfo("Schulungen", "+49 (0) 40 236 48 27-14", "akademie@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Hamburg", address = "Werner-Siemens-Straße 70, 22113 Hamburg", phoneNumber = "+49 (0)40 73 41 88-3", email = "hamburg@riwal.com"),
                    Depot(name = "Dortmund", address = "Giselherstraße 5-7, 44319 Dortmund", phoneNumber = "+49(0)231 - 927 30 55", email = "dortmund@riwal.com"),
                    Depot(name = "Hanau", address = "Bruchköbeler Landstraße 95, 63452 Hanau", phoneNumber = "+49 (0) 6181 307 66-0", email = "hanau@riwal.com"),
                    Depot(name = "Bingen", address = "Am Ockenheimer Graben 14, 55411 Bingen", phoneNumber = "+49 (0) 6721 987 48 0", email = "bingen@riwal.com"),
                    Depot(name = "Stuttgart", address = "Talstraße 30, 74379 Ingersheim", phoneNumber = "+49 (0) 7142 919 71 77", email = "stuttgart@riwal.com"),
                    Depot(name = "München", address = "Obere Hauptstraße 40, 85386 Eching", phoneNumber = "+49 (0) 89 993 99 75-0", email = "muenchen@riwal.com"),
                    Depot(name = "Zentrale", address = "Heidenkampsweg 45, 20097 Hamburg", phoneNumber = "+49 (0) 40 236 48 27-0", email = "info-de@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 56,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = false
    ),
    DK(
            picture = R.drawable.flag_dk,
            localizedNameRes = R.string.country_denmark,
            nextDayDeliveryCutoffTime = "07:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "16:30".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = true,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Udlejning", "+45 70 10 00 97", "lift_dk@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Salg af maskiner", "+45 29 68 06 15", "salg@riwal.com"),
                    ContactInfo("Teknisk helpdesk", "+45 88 17 81 17", "lift_dk@riwal.com"),
                    ContactInfo("Reservedele", "+45 70 10 00 97", "parts_dk@riwal.com"),
                    ContactInfo("Konduktør/opmåling", "+45 70 10 00 97", "lift_dk@riwal.com"),
                    ContactInfo("Administration", "+45 70 10 00 97", "lift_dk@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Odense", address = "P. L Brandts Allé 1, 5220 Odense SØ", phoneNumber = "+45 70 100 097", email = "bestilling@riwal.com"),
                    Depot(name = "Taastrup", address = "Taastrupgårdsvej 32, 2630 Taastrup", phoneNumber = "+45 70 100 097", email = "bestilling@riwal.com"),
                    Depot(name = "Århus", address = "Grydhøjparken 14B, 8381 Tilst", phoneNumber = "+45 70 100 097", email = "bestilling@riwal.com"),
                    Depot(name = "Aalborg", address = "Ølgodvej 15, 9220 Aalborg", phoneNumber = "+45 70 100 097", email = "bestilling@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 100,
            isGamingEnabled = true,
            gamingURL = "https://riwal.leadfamly.com/myriwal-app-game-icon",
            isMyProjectsEnabled = true,
            isMyQuotationEnabled = true
    ),
    ES(
            picture = R.drawable.flag_es,
            localizedNameRes = R.string.country_spain,
            nextDayDeliveryCutoffTime = "15:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "12:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = true,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Alquilar", "(+34) 902 104 200", "info-es@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Ventas", "(+34) 902 104 200", "info-es@riwal.com"),
                    ContactInfo("Mantenimiento", "(+34) 902 104 200", "info-es@riwal.com"),
                    ContactInfo("Piezas de repuesto", "(+34) 902 104 200", "info-es@riwal.com"),
                    ContactInfo("Formación", "(+34) 902 104 200", "info-es@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Delegación Barcelona", address = "Camí Antic de Vic, pacela 4, 08520 Corró d'Avall, Barcelona", phoneNumber = "(+34) 938 424 402", email = "info-es@riwal.com"),
                    Depot(name = "Delegación Madrid", address = "Av. de Cordoba, 5, 28341 Valdemoro, Madrid, Spain", phoneNumber = "(+34) 918 762 036", email = "info-es@riwal.com"),
                    Depot(name = "Oficina central España", address = "Polígono Industrial Castilla Vial 15 Parcela 15, 46380 Cheste Valencia", phoneNumber = "(+34) 902 104 200", email = "info-es@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 130,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = true
    ),
    FR(
            picture = R.drawable.flag_fr,
            localizedNameRes = R.string.country_france,
            nextDayDeliveryCutoffTime = "07:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "18:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = true,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Location de matériels", "09 77 42 11 00", "info_fr@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Vente de matériels", "+33 (2) 37 35 97 21", "ventes@riwal.com"),
                    ContactInfo("Riwal international", "+33 (2) 37 26 26 62 ", "info_fr@riwal.com"),
                    ContactInfo("Formation CACES & sécurité", "+33 (2) 37 26 35 15", "formation@riwal.com"),
                    ContactInfo("Assistance technique", "0825 950 450", "info_fr@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Riwal Bordeaux", address = "Rue Gagarine Z.I Airspace, 33 185 Le Haillan", phoneNumber = "05 56 08 10 00", email = "bordeaux@riwal.com"),
                    Depot(name = "Riwal Bourgogne", address = "Z.A le Prélong Rue de chez l'écuyer, 71 300 Monceau les Mines", phoneNumber = "03 85 69 06 66", email = "bourgogne@riwal.com"),
                    Depot(name = "Riwal Chartres", address = "Allée de la voie croix ZA du Bois Gueslin, 28 630 Mignières", phoneNumber = "02 37 35 20 00", email = "chartres@riwal.com"),
                    Depot(name = "Riwal Lille", address = "P.A des Chauffours, 62 710 Courrières", phoneNumber = "03 21 42 20 00", email = "lille@riwal.com"),
                    Depot(name = "Riwal Lyon", address = "Rue du Gaud Z.I des Ronzières, 69 530 Brignais", phoneNumber = "04 78 68 88 88", email = "lyon@riwal.com"),
                    Depot(name = "Riwal Marseille", address = "9 rue de la Glacière ZI les Bagnols, 13 127 Vitrolles", phoneNumber = "04 42 75 50 00", email = "marseille@riwal.com"),
                    Depot(name = "Riwal Nantes", address = "6 rue de la communauté, 44 140 Le Bignon", phoneNumber = "02 40 78 19 78", email = "nantes@riwal.com"),
                    Depot(name = "Riwal Paris Nord", address = "2 rue Louis Blériot, 93 290 Tremblay en France", phoneNumber = "01 49 89 02 44", email = "tremblay@riwal.com"),
                    Depot(name = "Riwal Paris Ouest", address = "1 rue de la petite Garenne, 78 920 Ecquevilly", phoneNumber = "01 49 89 02 20", email = "parisouest@riwal.com"),
                    Depot(name = "Riwal Paris Sud", address = "600 avenue de l'Europe, 77 240 Vert Saint Denis", phoneNumber = "01 64 09 50 66", email = "parissud@riwal.com"),
                    Depot(name = "Riwal Reims", address = "Route de Sainte-Ménéhoud, 51 360 Prunay", phoneNumber = "03 26 03 07 91", email = "reims@riwal.com"),
                    Depot(name = "Siège Social Riwal France", address = "Allée de la voie croix ZA du Bois Gueslin, 28 630 Mignières", phoneNumber = "02 37 24 10 00", email = "info_fr@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = false,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 140,
            isGamingEnabled = false,
            gamingURL = "https://go.drimify.com/map/22477/app.html?projectid=21202633705fbcd1fd0660c162675948-5fbcd1fd06613&lang=fr",
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = true
    ),
    GB(
            picture = R.drawable.flag_uk,
            localizedNameRes = R.string.country_united_kingdom,
            nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "15:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = true,
            rentalDeskContactInfo = listOf(
                    ContactInfo("HIRE DESK", "0844 335 2993", "info_uk@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("ALS SAFETY", "0800 7999257", null)
            ),
            depots = listOf(
//                    Depot(name = "UK Head office", address = "Unit D Whittle Close, Park Farm Industrial Estate, NN8 6TY Wellingborough", phoneNumber = "0844 3352993", email = "info_uk@riwal.com"),
                    Depot(name = "UK Head office", address = "DC7 Blossom Way, Prologis Maylands Ave Gateway,\n" +
                            "HP2 4ZB Hemel Hempstead, United KIngdom", phoneNumber = "0844 3352993", email = "info_uk@riwal.com"),
//                    Depot(name = "Bracknell Depot", address = "Unit 1 Downmill Road, Bracknell Business Park, RG12 1QS Bracknell", phoneNumber = "0844 335 2993", email = null),
                    Depot(name = "Kent Depot", address = "Unit 9 Platt Industrial Estate, Maidstone Road, St Mary Platt, TN15 8JA Kent", phoneNumber = "0844 335 2993", email = null),
//                    Depot(name = "Wellingborough Depot", address = "Unit D Whittle Close, Park Farm Industrial Estate, NN8 6TY Wellingborough", phoneNumber = "0844 335 2993", email = null),
                    Depot(name = "Wigan Depot", address = "9 Stephens Way, Goose Green, WN3 6PH Wigan", phoneNumber = "0844 335 2993", email = null)
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 73,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = true
    ),
    HR(
            picture = R.drawable.flag_hr,
            localizedNameRes = R.string.country_croatia,
            nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "15:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = true,
            isAccess4UAvailable = false,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Najam", "01 2763 581", "info-hr@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Prodaja", "091 332 00 44", "josip.jurjevic@riwal.com"),
                    ContactInfo("Održavanje", "091 332 00 58", "info-hr@riwal.com"),
                    ContactInfo("Rezevni dijelovi", "091 332 00 58", "info-hr@riwal.com"),
                    ContactInfo("Kontaktirajte nas", "01 2763 581", "info-hr@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "NAJAM RIWAL d.o.o. (sjedište)", address = "Bjelovarska 51, 10370 Dugo Selo", phoneNumber = null, email = null),
                    Depot(name = "NAJAM RIWAL d.o.o. (poslovnica)", address = "Stinice 26, 21000 Split", phoneNumber = null, email = null)
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 56,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = false
    ),
    IN(
            picture = R.drawable.flag_in,
            localizedNameRes = R.string.country_india,
            nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "15:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = false,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Rental", "+91 120 455 6342", "info@manliftgroup.com")

            ),
            otherContactInfo = listOf(
                    ContactInfo("Sales", "+91 120 455 6342", "sales@manliftgroup.com")
            ),
            depots = listOf(
                    Depot(
                            name = "Manlift India Private Limited - Head Office",
                            address = "1st Floor, Wegmans Business Park, Plot No.3, Knowledge Park-III, Greater Noida, Industrial Area, Dist. Gautam Buddh Nagar, Uttar Pradesh\n" +
                                    "PO Box: 201308 Uttar Pradesh",
                            coordinate = Coordinate(28.536741, 77.416631),
                            phoneNumber = "+91 1800 102 5438",
                            email = "india@manliftgroup.com"
                    ),
                    Depot(
                            name = "Manlift India Private Limited Noida - Yard",
                            address = "Plot no. 30, Surajpur Road Greater Noida, Gautam Buddh Nagar, Uttar Pradesh\n" +
                                    "PO Box: 201308 Uttar Pradesh",
                            coordinate = Coordinate(28.536741, 77.416631),
                            phoneNumber = "+91 1800 102 5438",
                            email = "india@manliftgroup.com"
                    ),
                    Depot(
                            name = "Manlift India Private Limited Gujarat - Yard",
                            address = "Plot No.-454, Chharodi Village Sanand, Veeramgam Highway\n" +
                                    "Ahmedabad - 382170 Gujarat",
                            coordinate = Coordinate(23.045638, 72.256547),
                            phoneNumber = "+91 8826401116",
                            email = "india@manliftgroup.com"
                    ),
                    Depot(
                            name = "Manlift India Private Limited Penukonda - Yard",
                            address = "Survey No. 454 / 2, NH - 44, Ammavaripalli Penukonda, Andhra Pradesh\n" +
                                    "515110 Penukonda",
                            coordinate = Coordinate(14.184655, 77.628511),
                            phoneNumber = "+91 1800 102 5438",
                            email = "india@manliftgroup.com"
                    )
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 56,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = false
    ),
    KZ(
            picture = R.drawable.flag_kz,
            localizedNameRes = R.string.country_kazakhstan,
            nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "15:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = false,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Аренда", "+7 701 027 89 56", "info.kz@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Продажа", "+7 701 027 89 56", "info.kz@riwal.com"),
                    ContactInfo("Сервис", "+7 701 206 92 49", "info.kz@riwal.com"),
                    ContactInfo("Parts", "+7 701 206 92 49", "info.kz@riwal.com"),
                    ContactInfo("Операторы", "+7 701 027 89 56", "info.kz@riwal.com"),
                    ContactInfo("Общая консультация", "+7 701 027 89 56", "info.kz@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Riwal Kazakhstan Headoffice", address = "Abulkhair Khan avenue 33, 060011 Atyrau", phoneNumber = "+7 7122 763 247", email = "info.kz@riwal.com"),
                    Depot(name = "Aksai Branch", address = "Molodezhnaya Street 11/2, 090300 Atyrau", phoneNumber = "+7 7122 763 247", email = "info.kz@riwal.com"),
                    Depot(name = "Tengiz Branch", address = "Rotational Village, Tengiz", phoneNumber = "+7 7122 763 247", email = "info.kz@riwal.com"),
                    Depot(name = "Astana Branch", address = "Promzone  Riwal Base, Astana", phoneNumber = "+7 7122 763 247", email = "info.kz@riwal.com"),
                    Depot(name = "Aktau Branch", address = "Promzone Riwal Base, 130000 Aktau", phoneNumber = "+7 7122 763 247", email = "info.kz@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = false,
            trainingFromURL = false,
            company = 56,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = false
    ),
    NL(
            picture = R.drawable.flag_nl,
            localizedNameRes = R.string.country_netherlands,
            nextDayDeliveryCutoffTime = "09:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "17:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = true,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Verhuur", "078 618 18 88", "verhuur@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Onderdelen", "078 620 68 88", "parts@riwal.com"),
                    ContactInfo("Onderhoud & Service", "078 620 68 66", "onderhoud@riwal.com"),
                    ContactInfo("Riwal Opleidingen", "078 620 68 68", "opleidingen@riwal.com"),
                    ContactInfo("Riwal Benelux", "088 618 18 88", "info@riwal.com"),
                    ContactInfo("Verkoop", "078 618 65 00", "verkoop@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Hoofdkantoor Riwal Benelux", address = "Maxwellstraat 27, 3316 GP Dordrecht", phoneNumber = "+31 (0)78 618 18 88", email = "info@riwal.com"),
                    Depot(name = "Vestiging Dordrecht", address = "Maxwellstraat 27, 3316 GP Dordrecht", phoneNumber = "+31 (0)78 654 39 22", email = "verhuurdordrecht@riwal.com"),
                    Depot(name = "Vestiging Europoort Rotterdam", address = "Moezelweg 118, 3198 LS Europoort Rotterdam", phoneNumber = "+31 (0)10 269 60 09", email = "verhuurrotterdam@riwal.com"),
                    Depot(name = "Vestiging Amsterdam", address = "Muiderstraatweg 63A, 1111 PV Diemen", phoneNumber = "+31 (0)20 449 69 99", email = "verhuuramsterdam@riwal.com"),
                    Depot(name = "Vestiging Groningen", address = "Potklei 6, 9351 VS Leek", phoneNumber = "+31 (0)50 201 17 00", email = "verhuurgroningen@riwal.com"),
                    Depot(name = "Vestiging Hengelo", address = "Vosboerweg 7, 7556 BT Hengelo", phoneNumber = "+31 (0)74 201 09 70", email = "verhuurhengelo@riwal.com"),
                    Depot(name = "Vestiging Zwolle", address = "Nervistraat 11, 8013 RS Zwolle", phoneNumber = "+31 (0)38 204 20 90", email = "verhuurzwolle@riwal.com"),
                    Depot(name = "Vestiging Eindhoven", address = "Ekkersrijt 3111, 5692 CD Son en Breugel", phoneNumber = "+31 (0)40 261 96 49", email = "verhuureindhoven@riwal.com"),
                    Depot(name = "Vestiging Genk, Riwal Belgium", address = "Winterbeeklaan 31, 3600 Genk", phoneNumber = "+32 (0)89 560 750", email = "verhuurgenk@riwal.com")
//                    Depot(name = "Vestiging Antwerpen, Riwal Belgium", address = "Jacobsveldweg 8, 2160 Wommelgem", phoneNumber = "+32 (0)3 354 37 87", email = "verhuurantwerpen@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = false,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = true,
            trainingURL = "https://riwalopleidingen.plusportdashboard.com",
            company = 15,
            isGamingEnabled = false,
            isMyProjectsEnabled = true,
            isMyQuotationEnabled = false
    ),
    NO(
            picture = R.drawable.flag_no,
            localizedNameRes = R.string.country_norway,
            nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "15:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = false,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Rental and Support", "+47 90 69 30 30", "Anne.marit.hagestande@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Riwal sentralbord", "+47 22 32 41 10", "lift_no@riwal.com"),
                    ContactInfo("Sales and Operations", "+47 90 04 74 74", "Karl.oskar.fevik@riwal.com"),
                    ContactInfo("Procurement and Parts", "+47 22 32 41 10", "parts_no@riwal.com"),
                    ContactInfo("Accountant", "+47 92 44 75 38", "anja.tjonnberg@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Riwal Norge", address = "Hellenvegen 7, 2022 Gjerdrum", phoneNumber = "+47 22 32 41 10", email = "lift_no@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = false,
            trainingFromURL = false,
            company = 110,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = false
    ),
    PL(
            picture = R.drawable.flag_pl,
            localizedNameRes = R.string.country_poland,
            nextDayDeliveryCutoffTime = "13:30".toLocalTime()!!,
            sameDayOffRentCutoffTime = "10:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = true,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Wynajem", "+48 46 895 13 63", "wynajem@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Sprzedaż", "+48 46 895 20 22", "sprzedaz@riwal.com"),
                    ContactInfo("Szkolenia", "+48 781 800 870", "szkolenia@riwal.com"),
                    ContactInfo("Serwis", "+48 781 800 091", "serwis@riwal.com"),
                    ContactInfo("Części", "+48 781 800 162", "czesci@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Oddział Szczecin", address = "ul. Wiosenna 28, 70-807 Szczecin", phoneNumber = "+48 91 443 99 58", email = "szczecin@riwal.com"),
                    Depot(name = "Oddział Pruszcz Gdański", address = "ul. Przemysłowa 3, 83-000 Pruszcz Gdański", phoneNumber = "+48 58 350 29 57", email = "trojmiasto@riwal.com"),
                    Depot(name = "Oddział Poznań", address = "ul. Hallera 6-8, 60-104 Poznań", phoneNumber = "+48 61 666 89 56", email = "poznan@riwal.com"),
                    Depot(name = "Oddział Warszawa", address = "Duchnice, ul. Żytnia 86, 05-850 Ożarów Mazowiecki", phoneNumber = "+48 22 230 99 50", email = "warszawa@riwal.com"),
                    Depot(name = "Oddział Łódź", address = "ul. Rokicińska 156A, 92-412 Łódź", phoneNumber = "+48 42 214 49 55", email = "lodz@riwal.com"),
                    Depot(name = "Oddział Wrocław", address = "ul. Brodzka 10A, 54-103 Wrocław", phoneNumber = "+48 71 889 89 54", email = "wroclaw@riwal.com"),
                    Depot(name = "Oddział Opole", address = "ul. Brodzka 10A, 54-103 Wrocław", phoneNumber = "+48 77 550 39 51", email = "opole@riwal.com"),
                    Depot(name = "Oddział Zabrze", address = "ul. Handlowa 10, 41-807 Zabrze", phoneNumber = "+48 32 630 69 53", email = "zabrze@riwal.com"),
                    Depot(name = "Oddział Kraków", address = "ul. Półłanki 21, 30-740 Kraków", phoneNumber = "+48 12 351 09 52", email = "krakow@riwal.com"),
                    Depot(name = "Rawa Mazowiecka", address = "ul. Zamkowa Wola 31a, 96-200, Rawa Mazowiecka", phoneNumber = "+48 46 895 13 63", email = "info-pl@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 160,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = true
    ),
    QA(
            picture = R.drawable.flag_qa,
            localizedNameRes = R.string.country_qatar,
            nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "15:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = false,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Rental", "+974 4411 6891", "info@manliftgroup.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Sales", "+974 4411 6891", "sales@manliftgroup.com")
            ),
            depots = listOf(
                    Depot(
                            name = "Manlift Qatar LLC - Access Platform Yard",
                            address = "Industrial Area, Zone: 57 551, Ar-Rayyan Doha",
                            coordinate = Coordinate(25.174387, 51.426050),
                            phoneNumber = "+974 4460 2436",
                            email = "qatar@manliftgroup.com"
                    ),
                    Depot(
                            name = "Manlift Power Qatar - Yard",
                            address = "Industrial Area, Zone: 38, Doha",
                            phoneNumber = "+974 4460 2436",
                            email = "qatar@manliftgroup.com"
                    )
            ),
            weekend = listOf(FRIDAY, SATURDAY),
            firstDayOfWeek = Calendar.SUNDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 56,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = false
    ),
    SE(
            picture = R.drawable.flag_se,
            localizedNameRes = R.string.country_sweden,
            nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "16:30".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = false,
            isAccess4UAvailable = true,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Uthyrning Malmö", "(+46) 010 516 40 60", "info.se@riwal.com"),
                    ContactInfo("Uthyrning Göteborg", "(+46) 031-381 92 00", "info.se@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Kundcenter", "(+46) 010 516 40 60", "info.se@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Riwal Göteborg", address = "Tuvevägen 35, 41705 Göteborg", phoneNumber = "+46(0)31-381 92 00", email = "info.se@riwal.com"),
                    Depot(name = "Riwal Malmö", address = "Flintyxegatan 3, 21376 Malmö", phoneNumber = "+46(0)40-655 49 00", email = "info.se@riwal.com")
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = true,
            trainingURL = "https://www.lipac.se/utbildning-kurser/",
            company = 120,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = true
    ),
    SI(
            picture = R.drawable.flag_si,
            localizedNameRes = R.string.country_slovenia,
            nextDayDeliveryCutoffTime = "12:00".toLocalTime()!!,
            sameDayOffRentCutoffTime = "15:00".toLocalTime()!!,
            isChatEnabled = false,
            isPhoneCallEnable = true,
            isAccess4UAvailable = false,
            rentalDeskContactInfo = listOf(
                    ContactInfo("Najem", "01 544 34 34", "info_si@riwal.com")
            ),
            otherContactInfo = listOf(
                    ContactInfo("Prodaja", "040 632 000", "miha.curin@riwal.com"),
                    ContactInfo( "Vzdrževanje in rezervni deli", "041 302 050", "servis.slovenia@riwal.com"),
                    ContactInfo("Usposabljanje za delo na višini", "01 544 34 34", "info_si@riwal.com"),
                    ContactInfo("Splošne informacije", "01 544 34 34", "info_si@riwal.com"),
                    ContactInfo("Servis", "041 302 050", "servis.slovenia@riwal.com")
            ),
            depots = listOf(
                    Depot(name = "Riwal najem opreme, d.o.o.", address = "Šmartinska cesta 32, 1000 Ljubljana", phoneNumber = null, email = null)
            ),
            weekend = listOf(SATURDAY, SUNDAY),
            firstDayOfWeek = Calendar.MONDAY,
            isBreakdownReportingEnabled = true,
            isAddAccessoriesEnabled = true,
            isBookingTrainingEnabled = true,
            trainingFromURL = false,
            company = 56,
            isGamingEnabled = false,
            isMyProjectsEnabled = false,
            isMyQuotationEnabled = false
    );

    val contactInfo
        get() = rentalDeskContactInfo + otherContactInfo

    companion object {
        val riwalCountries = listOf(BE, CORPORATE, DE, DK, ES, FR, GB, HR, KZ, NL, NO, PL, SE, SI)
        val manliftCountries = listOf(AE, IN, QA)
        val countriesForCurrentBrand = if (FLAVOR_brand == "riwal") riwalCountries else manliftCountries
        val defaultForCurrentBrand = if (FLAVOR_brand == "riwal") CORPORATE else null
    }
}
