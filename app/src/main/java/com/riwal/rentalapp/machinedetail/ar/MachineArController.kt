package com.riwal.rentalapp.machinedetail.ar

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.ar.sceneform.assets.RenderableSource.RecenterMode
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.arcore.directionTo
import com.riwal.rentalapp.common.extensions.arcore.distanceTo
import com.riwal.rentalapp.common.extensions.arcore.plus
import com.riwal.rentalapp.common.extensions.arcore.times
import com.riwal.rentalapp.common.extensions.core.clampTo
import com.riwal.rentalapp.common.extensions.core.elementAfter
import com.riwal.rentalapp.common.extensions.datetime.seconds
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.machinedetail.ar.MachineArView.*
import com.riwal.rentalapp.machinedetail.ar.MachineArView.DisplayMode.VIEW
import com.riwal.rentalapp.machinedetail.ar.MachineArView.OnboardingStep.*
import com.riwal.rentalapp.model.MachineMeshDescriptor
import kotlinx.coroutines.*
import org.joda.time.Duration
import kotlin.math.min

@RequiresApi(Build.VERSION_CODES.N)
class MachineArController(
        val view: MachineArView,
        private val firestoreArModelLoader: FirestoreArModelLoader,
        private val analytics: RentalAnalytics
) : MachineArView.DataSource, MachineArView.Delegate, FirestoreArModelLoader.Delegate, ViewLifecycleObserver, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/


    lateinit var machineMeshDescriptors: List<MachineMeshDescriptor>

    private var isTestingMode = false
    private var modelDescriptors: List<ModelDescriptor> = emptyList()
    private var displayMode = VIEW
    private val viewModeOnboardingSteps = listOf(FLOOR_DETECTION, MACHINE_MOVEMENT, MACHINE_ROTATION, MACHINE_POSITIONING, DISPLAY_MODE_SWITCHING, NONE)
    private val measureModeOnboardingSteps = listOf(WALL_DETECTION, MACHINE_DIMENSIONS_MOVEMENT, MACHINE_POSITIONING, NONE)
    private var completedOnboardingSteps: Set<OnboardingStep> = emptySet()

    private val modelScale
        get() = if (isTestingMode) 1.0f / 20.0f else 1.0f

    // FIXME: When you set recenterMode to NONE, the scale is ignored.
    // This is a bug in ArCore <= SDK 1.10.0, check if it is fixed in future releases
    private val modelRecenterMode
        get() = if (isTestingMode) RecenterMode.ROOT else RecenterMode.NONE

    private var activeModelDescriptor: ModelDescriptor? = null
        set(value) {
            field = value
            view.notifyDataChanged()
        }

    private var currentOnboardingStep = FLOOR_DETECTION
        set(value) {
            field = value
            view.notifyDataChanged()
        }

    private val isLoading
        get() = loadingProgress < 1.0

    private val loadingProgress
        get() = activeModelDescriptor?.loadingProgress ?: 0.0

    private val distanceFromMachineToUserAtMachinePlacement
        get() = 2f * modelScale

    private val minDistanceFromMachineToUser
        get() = machineWidth

    private val maxDistanceFromMachineToUser
        get() = 25f

    private val machineBoundingBoxSize
        get() = (activeModelDescriptor!!.renderable?.collisionShape as? Box)?.size

    private val machineWidth: Float
        get() {
            val size = machineBoundingBoxSize ?: return 0f
            return min(size.x, size.z)
        }

    private val machineHeight
        get() = machineBoundingBoxSize?.y ?: 0f


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {

        view.dataSource = this
        view.delegate = this

        firestoreArModelLoader.delegate = this

        observe(view)
    }

    override fun onViewCreate() {
        modelDescriptors = machineMeshDescriptors.map { ModelDescriptor(meshDescriptor = it) }
        activeModelDescriptor = modelDescriptors.first()

        loadModelIfNeeded(activeModelDescriptor!!)
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        cancel()
    }


    /*------------------------------- Machine AR View Data Source --------------------------------*/


    override fun activeModelDescriptor(view: MachineArView) = activeModelDescriptor!!
    override fun currentOnboardingStep(view: MachineArView) = currentOnboardingStep
    override fun displayMode(view: MachineArView) = displayMode
    override fun machineWidth(view: MachineArView) = machineWidth
    override fun machineHeight(view: MachineArView) = machineHeight
    override fun isLoading(view: MachineArView) = isLoading
    override fun loadingProgress(view: MachineArView) = loadingProgress

    override fun initialMachinePosition(view: MachineArView, cameraPosition: Vector3, cameraDirection: Vector3): Vector3 {
        val initialMachinePosition = cameraPosition + (cameraDirection * 0.001f)
        return userFriendlyMachinePosition(initialMachinePosition, cameraPosition, distanceFromMachineToUserAtMachinePlacement, maxDistanceFromMachineToUser)
    }

    override fun userFriendlyMachinePosition(view: MachineArView, desiredMachinePosition: Vector3, cameraPosition: Vector3): Vector3 {
        return userFriendlyMachinePosition(desiredMachinePosition, cameraPosition, minDistanceFromMachineToUser, maxDistanceFromMachineToUser)
    }


    /*--------------------------------- Machine AR View Delegate ---------------------------------*/


    override fun onFloorDetected(view: MachineArView) {
        logOnBoardingStepCompletedIfNeeded(FLOOR_DETECTION)
        completedOnboardingSteps += FLOOR_DETECTION
        updateCurrentOnboardingStep()
    }

    override fun onMachineMoved(view: MachineArView) {
        logOnBoardingStepCompletedIfNeeded(MACHINE_MOVEMENT)
        completedOnboardingSteps += MACHINE_MOVEMENT
        if (currentOnboardingStep == MACHINE_MOVEMENT) {
            updateCurrentOnboardingStep(delay = 2.seconds())
        }
    }

    override fun onRotateMachineSelected(view: MachineArView) {
        logOnBoardingStepCompletedIfNeeded(MACHINE_ROTATION)
        completedOnboardingSteps += MACHINE_ROTATION
        if (currentOnboardingStep == MACHINE_ROTATION) {
            updateCurrentOnboardingStep(delay = 2.seconds())
        }
    }

    override fun onNextPositionSelected(view: MachineArView) {
        activeModelDescriptor = modelDescriptors.elementAfter(activeModelDescriptor, wrap = true)
        loadModelIfNeeded(activeModelDescriptor!!)

        logOnBoardingStepCompletedIfNeeded(MACHINE_POSITIONING)
        completedOnboardingSteps += MACHINE_POSITIONING
        if (currentOnboardingStep == MACHINE_POSITIONING) {
            updateCurrentOnboardingStep(delay = 4.seconds())
        }
    }

    override fun onDisplayModeChanged(view: MachineArView, displayMode: DisplayMode) {
        this.displayMode = displayMode

        logOnBoardingStepCompletedIfNeeded(DISPLAY_MODE_SWITCHING)
        completedOnboardingSteps += DISPLAY_MODE_SWITCHING
        updateCurrentOnboardingStep()
    }

    override fun onWallDetected(view: MachineArView) {
        logOnBoardingStepCompletedIfNeeded(WALL_DETECTION)
        completedOnboardingSteps += WALL_DETECTION
        updateCurrentOnboardingStep()
    }

    override fun onMachineDimensionsMoved(view: MachineArView) {
        logOnBoardingStepCompletedIfNeeded(MACHINE_DIMENSIONS_MOVEMENT)
        completedOnboardingSteps += MACHINE_DIMENSIONS_MOVEMENT
        if (currentOnboardingStep == MACHINE_DIMENSIONS_MOVEMENT) {
            updateCurrentOnboardingStep()
        }
    }

    override fun onOnboardingStepDismissed(view: MachineArView, onboardingStep: OnboardingStep) {
        completedOnboardingSteps += onboardingStep
        updateCurrentOnboardingStep()
    }

    override fun onNavigateBackSelected(view: MachineArView) {
        view.navigateBack()
    }


    /*---------------------------- Firestore AR Model Loader Delegate ----------------------------*/


    override fun onLoadingProgressUpdate(loader: FirestoreArModelLoader, url: String, progress: Double) {
        modelDescriptorForFirestoreUrl(url)!!.loadingProgress = progress
        view.notifyDataChanged()
    }


    /*------------------------------------- Private Methods --------------------------------------*/


    private fun userFriendlyMachinePosition(desiredMachinePosition: Vector3, cameraPosition: Vector3, minDistanceToCamera: Float, maxDistanceToCamera: Float): Vector3 {

        val cameraPositionAtLocationHeight = Vector3(cameraPosition)
        cameraPositionAtLocationHeight.y = desiredMachinePosition.y

        val distanceFromCameraToLocation = cameraPositionAtLocationHeight.distanceTo(desiredMachinePosition)
        val directionFromCameraToLocation = cameraPositionAtLocationHeight.directionTo(desiredMachinePosition)
        val limitedDistanceFromCameraToLocation = distanceFromCameraToLocation.clampTo(minDistanceToCamera..maxDistanceToCamera)

        return cameraPositionAtLocationHeight + directionFromCameraToLocation * limitedDistanceFromCameraToLocation
    }

    private fun updateCurrentOnboardingStep(delay: Duration) {
        currentOnboardingStep = NONE
        launch {
            delay(delay.millis)
            updateCurrentOnboardingStep()
        }
    }

    private fun updateCurrentOnboardingStep() {
        val onboardingSteps = if (displayMode == VIEW) viewModeOnboardingSteps else measureModeOnboardingSteps
        currentOnboardingStep = onboardingSteps.first { it !in completedOnboardingSteps }
    }

    private fun modelDescriptorForFirestoreUrl(url: String) = modelDescriptors.firstOrNull { it.meshDescriptor.url == url }

    private fun loadModelIfNeeded(modelDescriptor: ModelDescriptor) {

        if (modelDescriptor.renderable != null) {
            return
        }

        launch {
            try {
                modelDescriptor.renderable = firestoreArModelLoader.loadArModel(modelDescriptor.meshDescriptor.url, scale = modelScale, recenterMode = modelRecenterMode)
            } catch (error: Exception) {
                view.notifyLoadingError(error)
            } finally {
                view.notifyDataChanged()
            }
        }

    }

    private fun logOnBoardingStepCompletedIfNeeded(onboardingStep: OnboardingStep) {
        if (onboardingStep in completedOnboardingSteps) {
            return
        }
        analytics.onBoardingStepInArCompleted(onboardingStep)
    }

}