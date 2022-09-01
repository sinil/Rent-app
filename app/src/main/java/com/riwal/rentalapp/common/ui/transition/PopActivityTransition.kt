package com.riwal.rentalapp.common.ui.transition

import com.riwal.rentalapp.R.anim.pop_to_right
import com.riwal.rentalapp.R.anim.reveal_from_left

object PopActivityTransition : BasicActivityTransition(enterAnimationRes = reveal_from_left, exitAnimationRes = pop_to_right)