package com.riwal.rentalapp.common.ui

import androidx.transition.AutoTransition
import androidx.transition.TransitionSet


class ParallelAutoTransition : AutoTransition() {

    init {
        ordering = TransitionSet.ORDERING_TOGETHER
    }
}