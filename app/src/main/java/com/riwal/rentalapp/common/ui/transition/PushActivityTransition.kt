package com.riwal.rentalapp.common.ui.transition

import com.riwal.rentalapp.R.anim.overlap_to_left
import com.riwal.rentalapp.R.anim.push_from_right

object PushActivityTransition : BasicActivityTransition(enterAnimationRes = push_from_right, exitAnimationRes = overlap_to_left)