package com.riwal.rentalapp.model.api


data class TrainingCourse(
        val Id: String,
        val Name: String,
        val Duration: String,
        val Certification: String,
        val Depots: List<TrainingDepot>,
        val CourseDepots: String,
        val Participants: Participant,
        val Price: String,
        val ShortDesc: String,
        val LongDesc: String,
        val Category: CategoryResp
) {

    data class CategoryResp(val Id: String,
                            val Name: String,
                            val ImageUrl: String?,
                            val Description: String)
}

