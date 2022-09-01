package com.riwal.rentalapp.common.ui.transition

import com.riwal.rentalapp.R.anim.fade_in
import com.riwal.rentalapp.R.anim.fade_out

object FadeActivityTransition : BasicActivityTransition(enterAnimationRes = fade_in, exitAnimationRes = fade_out)