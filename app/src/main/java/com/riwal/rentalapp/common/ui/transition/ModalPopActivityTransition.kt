package com.riwal.rentalapp.common.ui.transition

import com.riwal.rentalapp.R.anim.reveal_from_back
import com.riwal.rentalapp.R.anim.slide_down

object ModalPopActivityTransition : BasicActivityTransition(enterAnimationRes = reveal_from_back, exitAnimationRes = slide_down)