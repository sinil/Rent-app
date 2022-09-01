package com.riwal.rentalapp.common.ui.transition

import com.riwal.rentalapp.R.anim.overlap_to_back
import com.riwal.rentalapp.R.anim.slide_up

object ModalPushActivityTransition : BasicActivityTransition(enterAnimationRes = slide_up, exitAnimationRes = overlap_to_back)