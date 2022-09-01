package com.riwal.rentalapp.model.api

data class TrainingCategory(val Id: String,
                    val Name: String,
                    val ImageUrl: String?,
                    val Description: String,
                    val Courses: List<TrainingCourse>)