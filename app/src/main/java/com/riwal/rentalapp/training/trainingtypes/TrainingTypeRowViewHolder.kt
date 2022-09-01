package com.riwal.rentalapp.training.trainingtypes

import android.view.View
import com.riwal.rentalapp.BuildConfig
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.api.TrainingCategory
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_training_category.*
import kotlinx.android.synthetic.main.row_training_category.trainingCategoryImageView
import kotlinx.android.synthetic.main.view_training.*

class TrainingTypeRowViewHolder (override val containerView: View) : EasyRecyclerView.ViewHolder(containerView), LayoutContainer {

    fun updateWith(trainingCategory: TrainingCategory) {
        trainingCategoryName.text = trainingCategory.Name
        trainingCategoryImageView.loadImage(BuildConfig.MY_RIWAL_URL + trainingCategory.ImageUrl, placeholder = R.drawable.img_machines_contour)


    }
}