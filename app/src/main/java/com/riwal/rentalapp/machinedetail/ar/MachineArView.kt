package com.riwal.rentalapp.machinedetail.ar

import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.MachineMeshDescriptor

interface MachineArView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun notifyLoadingError(error: Throwable)
    fun navigateBack()

    interface DataSource {
        fun activeModelDescriptor(view: MachineArView): ModelDescriptor
        fun currentOnboardingStep(view: MachineArView): OnboardingStep
        fun displayMode(view: MachineArView): DisplayMode
        fun machineWidth(view: MachineArView): Float
        fun machineHeight(view: MachineArView): Float
        fun isLoading(view: MachineArView): Boolean
        fun loadingProgress(view: MachineArView): Double
        fun initialMachinePosition(view: MachineArView, cameraPosition: Vector3, cameraDirection: Vector3): Vector3
        fun userFriendlyMachinePosition(view: MachineArView, desiredMachinePosition: Vector3, cameraPosition: Vector3): Vector3
    }

    interface Delegate {
        fun onFloorDetected(view: MachineArView)
        fun onMachineMoved(view: MachineArView)
        fun onRotateMachineSelected(view: MachineArView)
        fun onNextPositionSelected(view: MachineArView)
        fun onWallDetected(view: MachineArView)
        fun onMachineDimensionsMoved(view: MachineArView)
        fun onDisplayModeChanged(view: MachineArView, displayMode: DisplayMode)
        fun onOnboardingStepDismissed(view: MachineArView, onboardingStep: OnboardingStep)
        fun onNavigateBackSelected(view: MachineArView)
    }

    data class ModelDescriptor(val meshDescriptor: MachineMeshDescriptor, var renderable: ModelRenderable? = null, var loadingProgress: Double = 0.0)

    enum class OnboardingStep {
        FLOOR_DETECTION,
        MACHINE_MOVEMENT,
        MACHINE_ROTATION,
        MACHINE_POSITIONING,
        DISPLAY_MODE_SWITCHING,
        WALL_DETECTION,
        MACHINE_DIMENSIONS_MOVEMENT,
        NONE
    }

    enum class DisplayMode {
        VIEW,
        MEASURE
    }

}
