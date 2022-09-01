package com.riwal.rentalapp.training.traininglist

import android.view.View
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.model.api.TrainingCourse
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_training.*

class TrainingListRowViewHolder (override val containerView: View) : EasyRecyclerView.ViewHolder(containerView), LayoutContainer {

    fun updateWith(trainingCourse: TrainingCourse, delegate: TrainingListView.Delegate) {
        trainingName.text = trainingCourse.Name
        durationTextView.text = trainingCourse.Duration.trim()
        if(trainingCourse.Certification != null) {
            certificationTextView.text =trainingCourse.Certification.trim()
        }else {
            certificationLabel.visibility = View.GONE
            certificationTextView.visibility = View.GONE
        }
        participantTextView.text = trainingCourse.Participants.StringValue.trim()
        priceTextView.text = trainingCourse.Price.trim()
        detailsButton.setOnClickListener { delegate.onTrainingDetailSelected(trainingCourse) }



    }
}