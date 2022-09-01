package com.riwal.rentalapp.machinedetail.ar

import android.annotation.SuppressLint
import android.graphics.Color.BLACK
import android.graphics.Color.TRANSPARENT
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import com.google.ar.core.Anchor
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Ray
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.arcore.*
import com.riwal.rentalapp.common.arcore.MaterialFactory.ShadingAndBlendingMode.UNLIT_TRANSPARENT
import com.riwal.rentalapp.common.arcore.PlaneAnchorNode.Alignment.HORIZONTAL
import com.riwal.rentalapp.common.arcore.PlaneAnchorNode.Alignment.VERTICAL
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.dp
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.arcore.*
import com.riwal.rentalapp.common.extensions.core.PI
import com.riwal.rentalapp.common.extensions.core.formatWithDigits
import com.riwal.rentalapp.common.extensions.core.toDegrees
import com.riwal.rentalapp.common.ui.GestureDetector
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.machinedetail.ar.MachineArView.DisplayMode.MEASURE
import com.riwal.rentalapp.machinedetail.ar.MachineArView.DisplayMode.VIEW
import com.riwal.rentalapp.machinedetail.ar.MachineArView.OnboardingStep
import com.riwal.rentalapp.machinedetail.ar.MachineArView.OnboardingStep.*
import com.tooltip.Tooltip
import kotlinx.android.synthetic.main.view_machine_ar.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class MachineArViewImpl : RentalAppNotificationActivity(), MachineArView, ArFragment.Delegate, ArHelpView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: MachineArView.DataSource
    override lateinit var delegate: MachineArView.Delegate

    private val floorPlaneNode = Node()
    private val machineNode = Node()
    private val wallPlaneNode = Node()
    private val machineDimensionsNode = Node() // This node is the parent of machineDimensionsPlaneNode and is used as a coordinate system for placing the dimension labels
    private val machineDimensionsPlaneNode = Node()

    private var tooltip: Tooltip? = null
    private var tooltipText: String? = null

    private lateinit var arFragment: ArFragment
    private val shapeFactory = ShapeFactory()
    private var machineRotation = 0f
    private var isHelpPageVisible = false
        set(value) {
            field = value
            updateUI()
        }

    private val activeModelDescriptor
        get() = dataSource.activeModelDescriptor(view = this)

    private val currentOnboardingStep
        get() = dataSource.currentOnboardingStep(view = this)

    private val displayMode
        get() = dataSource.displayMode(view = this)

    private val machineWidth
        get() = dataSource.machineWidth(view = this)

    private val machineHeight
        get() = dataSource.machineHeight(view = this)

    private val isLoading
        get() = dataSource.isLoading(view = this)

    private val loadingProgress
        get() = dataSource.loadingProgress(view = this)

    private val initialMachinePosition
        get() = dataSource.initialMachinePosition(view = this, cameraPosition = camera.worldPosition, cameraDirection = camera.direction)

    private val sceneView
        get() = arFragment.arSceneView

    private val sceneRootNode
        get() = sceneView.scene

    private val currentArFrame
        get() = sceneView.arFrame

    private val camera
        get() = currentArFrame!!.camera

    private val horizontalPlaneAnchorNodes
        get() = sceneRootNode.children
                .mapNotNull { it as? PlaneAnchorNode }
                .filter { it.alignment == HORIZONTAL }

    private val verticalPlaneAnchorNodes
        get() = sceneRootNode.children
                .mapNotNull { it as? PlaneAnchorNode }
                .filter { it.alignment == VERTICAL }

    private val largestHorizontalPlaneAnchorNode
        get() = horizontalPlaneAnchorNodes.maxBy { it.squareMeters }

    private val largestVerticalPlaneAnchorNode
        get() = verticalPlaneAnchorNodes.maxBy { it.squareMeters }

    private val loadingPercentage
        get() = (loadingProgress * 100).toInt()

    private val hasDetectedFloorPlane
        get() = horizontalPlaneAnchorNodes.isNotEmpty()

    private val hasPlacedMachine
        get() = machineNode.parent != null

    private val hasDetectedWallPlane
        get() = verticalPlaneAnchorNodes.isNotEmpty()

    private val hasPlacedMachineDimensions
        get() = machineDimensionsNode.parent != null


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_machine_ar)

        helpView.delegate = this

        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment
        arFragment.delegate = this

        val gestureDetector = GestureDetector(this)
        gestureDetector.setOnPanListener { event -> onPanGestureInArView(event); true }
        sceneView.setOnTouchListener(gestureDetector)

        arMode3DButton.setOnClickListener { delegate.onDisplayModeChanged(view = this, displayMode = VIEW) }
        arModeMeasureButton.setOnClickListener { delegate.onDisplayModeChanged(view = this, displayMode = MEASURE) }
        rotateCounterClockwiseButton.setOnClickListener { delegate.onRotateMachineSelected(view = this) }
        rotateClockwiseButton.setOnClickListener { delegate.onRotateMachineSelected(view = this) }
        nextPositionButton.setOnClickListener { delegate.onNextPositionSelected(view = this) }
        closeButton.setOnClickListener { delegate.onNavigateBackSelected(view = this) }
        helpButton.setOnClickListener { isHelpPageVisible = true }

        launch {

            val materialFactory = MaterialFactory(this@MachineArViewImpl)

            floorPlaneNode.renderable = shapeFactory.makeFloor(getMaterial(com.google.ar.sceneform.rendering.R.raw.sceneform_plane_shadow_material))
            floorPlaneNode.renderable!!.isShadowCaster = false

            val wallPlaneMaterial = materialFactory.makeMaterial(UNLIT_TRANSPARENT)
            wallPlaneMaterial.setBaseColor(TRANSPARENT)
            wallPlaneNode.renderable = shapeFactory.makePlane(width = 100f, height = 100f, material = wallPlaneMaterial)
            wallPlaneNode.renderable!!.isShadowCaster = false

            machineDimensionsNode.addChild(machineDimensionsPlaneNode)

            val machineDimensionsMaterial = materialFactory.makeMaterial(UNLIT_TRANSPARENT)
            machineDimensionsMaterial.setBaseColor(r = 1f, g = 1f, b = 1f, a = 0.6f)
            machineDimensionsPlaneNode.renderable = shapeFactory.makePlane(width = 1f, height = 1f, material = machineDimensionsMaterial)
            machineDimensionsPlaneNode.renderable!!.isShadowCaster = false
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun updateUI(animated: Boolean) {

        val isInMeasureMode = displayMode == MEASURE
        val isInViewMode = displayMode == VIEW

        placeMachineIfNeeded()
        placeMachineOutlineIfNeeded()

        machineNode.renderable = activeModelDescriptor.renderable
        machineNode.isVisible = isInViewMode
        machineDimensionsNode.isVisible = isInMeasureMode
        machineDimensionsPlaneNode.localScale = Vector3(machineWidth, machineHeight, 1f)

        measurementLabelsOverlay.widthText = "← ${machineWidth.formatWithDigits(2)} →"
        measurementLabelsOverlay.heightText = "← ${machineHeight.formatWithDigits(2)} →"
        measurementLabelsOverlay.isVisible = isInMeasureMode && hasPlacedMachineDimensions && !isLoading

        val machineNeededButIsStillLoading = currentOnboardingStep !in listOf(FLOOR_DETECTION, WALL_DETECTION) && isLoading
        onboardingView.isVisible = currentOnboardingStep != NONE && !machineNeededButIsStillLoading
        onboardingTitleTextView.text = titleFor(currentOnboardingStep)
        onboardingTitleTextView.setShadowLayer(1f, 0f, dp(1f), BLACK)
        onboardingSubtitleTextView.text = getString(R.string.ar_tutorial_subtitle_wall_detection)
        onboardingSubtitleTextView.isVisible = currentOnboardingStep == WALL_DETECTION
        onboardingSubtitleTextView.setShadowLayer(1f, 0f, dp(1f), BLACK)

        floorGraphicView.isVisible = currentOnboardingStep in listOf(FLOOR_DETECTION, MACHINE_MOVEMENT)
        wallGraphicView.isVisible = currentOnboardingStep in listOf(WALL_DETECTION, MACHINE_DIMENSIONS_MOVEMENT)
        scanOnboardingView.isVisible = currentOnboardingStep in listOf(FLOOR_DETECTION, WALL_DETECTION)
        moveOnboardingView.isVisible = currentOnboardingStep in listOf(MACHINE_MOVEMENT, MACHINE_DIMENSIONS_MOVEMENT)
        helpButton.isVisible = currentOnboardingStep in listOf(FLOOR_DETECTION, WALL_DETECTION)

        displayModeSegmentedControl.isVisible = currentOnboardingStep !in listOf(FLOOR_DETECTION, MACHINE_MOVEMENT)
        rotateClockwiseButton.isVisible = isInViewMode && !isLoading && currentOnboardingStep !in listOf(FLOOR_DETECTION, MACHINE_MOVEMENT)
        rotateCounterClockwiseButton.isVisible = isInViewMode && !isLoading && currentOnboardingStep !in listOf(FLOOR_DETECTION, MACHINE_MOVEMENT)
        nextPositionButton.isVisible = !isLoading && currentOnboardingStep !in listOf(FLOOR_DETECTION, MACHINE_MOVEMENT, WALL_DETECTION, MACHINE_DIMENSIONS_MOVEMENT)

        statusTextView.text = getString(R.string.machine_in_ar_loading_progress, "$loadingPercentage")
        statusBar.isVisible = isLoading && currentOnboardingStep !in listOf(FLOOR_DETECTION, WALL_DETECTION)

        helpView.isVisible = isHelpPageVisible

        if (currentOnboardingStep == MACHINE_ROTATION && !isLoading) {
            showTooltip(text = getString(R.string.ar_onboarding_message_rotate_machine), anchorView = rotateCounterClockwiseButton, gravity = Gravity.TOP)
        } else if (currentOnboardingStep == MACHINE_POSITIONING && !isLoading) {
            showTooltip(text = getString(R.string.ar_onboarding_message_change_machine_position), anchorView = nextPositionButton, gravity = Gravity.TOP)
        } else if (currentOnboardingStep == DISPLAY_MODE_SWITCHING && !isLoading) {
            showTooltip(text = getString(R.string.ar_onboarding_message_measure_mode), anchorView = arModeMeasureButton, gravity = Gravity.BOTTOM)
        } else {
            hideTooltip()
        }
    }

    override fun onBackPressed() {
        if (isHelpPageVisible) {
            isHelpPageVisible = false
        } else {
            delegate.onNavigateBackSelected(view = this)
        }
    }


    /*-------------------------------------- Machine AR View -------------------------------------*/


    override fun notifyLoadingError(error: Throwable) = showInformativeMessage(R.string.ar_machine_model_loading_failed)
    override fun navigateBack() = finish(transition = ModalPopActivityTransition)


    /*----------------------------------- AR Fragment Delegate -----------------------------------*/


    override fun onNodeAddedForAnchor(arFragment: ArFragment, node: Node, anchor: Anchor) {

        if (node !is PlaneAnchorNode) {
            return
        }

        if (node.alignment == HORIZONTAL) {
            setFloorNodeAsChildOfLargestHorizontalPlaneNode()
            delegate.onFloorDetected(view = this)
        }

        if (node.alignment == VERTICAL) {
            setWallNodeAsChildOfLargestVerticalPlaneNode()
            delegate.onWallDetected(view = this)
        }

    }

    override fun onUpdate(arFragment: ArFragment, frameTime: FrameTime) {

        val elapsedTime = frameTime.deltaSeconds

        val rotationSpeed = Float.PI / 2
        val rotationDelta = rotationSpeed * elapsedTime

        if (rotateCounterClockwiseButton.isPressed) {
            machineRotation += rotationDelta
        }

        if (rotateClockwiseButton.isPressed) {
            machineRotation -= rotationDelta
        }

        machineNode.localRotation = Quaternion.axisAngle(Vector3.up(), machineRotation.toDegrees())

        updateMachineDimensions()
        snapMachineToFloor()
    }


    /*----------------------------------- AR Help View Delegate ----------------------------------*/


    override fun onDismissSelected(view: ArHelpView) {
        isHelpPageVisible = false
    }


    /*-------------------------------------- Private methods -------------------------------------*/


    private fun onPanGestureInArView(event: MotionEvent) {

        if (isLoading) {
            return
        }

        val hitTestResults = sceneRootNode.hitTestAll(event)
        val relevantHitTestResults = hitTestResults.filter { it.node in listOf(floorPlaneNode, wallPlaneNode) }

        if (relevantHitTestResults.isEmpty()) {
            return
        }

        val nearestHitResult = relevantHitTestResults.first()
        val isTouchingFloor = nearestHitResult.node == floorPlaneNode
        val isTouchingWall = nearestHitResult.node == wallPlaneNode
        val touchHitPosition = nearestHitResult.point

        if (isTouchingFloor) {

            moveMachineTo(touchHitPosition)
            delegate.onMachineMoved(view = this)

        } else if (isTouchingWall) {

            val position = Vector3(touchHitPosition)
            position.y = floorPlaneNode.worldPosition.y
            moveMachineTo(position)
            delegate.onMachineDimensionsMoved(view = this)

        }

    }

    private fun moveMachineToInitialPosition() {
        machineNode.worldPosition = initialMachinePosition
    }

    private fun moveMachineTo(position: Vector3) {
        machineNode.worldPosition = userFriendlyMachinePositionFor(position)
    }

    private fun userFriendlyMachinePositionFor(desiredPosition: Vector3): Vector3 {
        return dataSource.userFriendlyMachinePosition(view = this, desiredMachinePosition = desiredPosition, cameraPosition = camera.worldPosition)
    }

    private fun snapMachineToFloor() {
        machineNode.localPosition = machineNode.localPosition.apply { y = 0f }
    }

    private fun setFloorNodeAsChildOfLargestHorizontalPlaneNode() {
        if (largestHorizontalPlaneAnchorNode == null) {
            return
        }
        largestHorizontalPlaneAnchorNode!!.addChild(floorPlaneNode)
    }

    private fun setWallNodeAsChildOfLargestVerticalPlaneNode() {
        if (largestVerticalPlaneAnchorNode == null) {
            return
        }
        largestVerticalPlaneAnchorNode!!.addChild(wallPlaneNode)
    }

    private fun placeMachineIfNeeded() {
        if (!hasDetectedFloorPlane || hasPlacedMachine) {
            return
        }

        floorPlaneNode.addChild(machineNode)
        moveMachineToInitialPosition()
    }

    private fun placeMachineOutlineIfNeeded() {
        if (!hasDetectedWallPlane || hasPlacedMachineDimensions) {
            return
        }

        wallPlaneNode.addChild(machineDimensionsNode)
        moveMachineToInitialPosition()
    }

    private fun updateMachineDimensions() {

        if (largestVerticalPlaneAnchorNode == null || machineNode.renderable == null || !machineDimensionsNode.isVisible) {
            return
        }

        val machineWorldCenter = Vector3(machineNode.worldPosition)
        machineWorldCenter.y = machineNode.worldCenter.y
        val machineWorldCenterOnWall = perpendicularProjectionOfPositionOnNode(machineWorldCenter, wallPlaneNode) ?: machineWorldCenter

        machineDimensionsNode.worldPosition = machineWorldCenterOnWall

        val distanceFromCameraToWall = camera.worldPosition.distanceTo(machineWorldCenterOnWall)
        val margin = 0.03f * distanceFromCameraToWall

        val xOffset = Vector3(0.001f, 0f, 0f)
        val widthTextPositionOnOutline = Vector3(0f, -machineHeight / 2 + margin, 0f)
        val widthTextOffsetPositionInScreenSpace1 = machineDimensionsNode.convertPositionToScreenSpace(widthTextPositionOnOutline - xOffset, sceneRootNode.camera).xy
        val widthTextOffsetPositionInScreenSpace2 = machineDimensionsNode.convertPositionToScreenSpace(widthTextPositionOnOutline + xOffset, sceneRootNode.camera).xy

        val yOffset = Vector3(0f, 0.001f, 0f)
        val heightTextPositionOnOutline = Vector3(machineWidth / 2 - margin, 0f, 0f)
        val heightTextOffsetPositionInScreenSpace1 = machineDimensionsNode.convertPositionToScreenSpace(heightTextPositionOnOutline - yOffset, sceneRootNode.camera).xy
        val heightTextOffsetPositionInScreenSpace2 = machineDimensionsNode.convertPositionToScreenSpace(heightTextPositionOnOutline + yOffset, sceneRootNode.camera).xy

        runOnUiThread {
            measurementLabelsOverlay.centerAndOrientWidthTextViewBetweenPoints(widthTextOffsetPositionInScreenSpace1, widthTextOffsetPositionInScreenSpace2)
            measurementLabelsOverlay.centerAndOrientHeightTextViewBetweenPoints(heightTextOffsetPositionInScreenSpace1, heightTextOffsetPositionInScreenSpace2)
        }

    }

    private fun perpendicularProjectionOfPositionOnNode(position: Vector3, node: Node): Vector3? {
        var front = node.forward
        front.y = 0f
        front = front.normalized()

        val hitTestResults = sceneRootNode.hitTestAll(Ray(position - (front * 1000f), front))
        hitTestResults += sceneRootNode.hitTestAll(Ray(position + (front * 1000f), front))

        return hitTestResults.firstOrNull { it.node == node }?.point
    }

    private fun showTooltip(text: String, anchorView: View, gravity: Int) {

        if (text != tooltipText) {
            hideTooltip()
        }

        tooltip = Tooltip.Builder(anchorView)
                .setText(text)
                .setGravity(gravity)
                .setDismissOnClick(true)
                .setCornerRadius(dp(2f))
                .setPadding(dp(16))
                .setBackgroundColor(color(R.color.black))
                .setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1)
                .setTextColor(color(R.color.material_text_high_emphasis_on_dark))
                .setOnClickListener { delegate.onOnboardingStepDismissed(view = this, onboardingStep = currentOnboardingStep) }
                .build()

        tooltipText = text

        tooltip!!.show()
    }

    private fun hideTooltip() {
        tooltip?.dismiss()
        tooltipText = null
    }

    private fun titleFor(onboardingStep: OnboardingStep) = when (onboardingStep) {
        FLOOR_DETECTION -> getString(R.string.ar_tutorial_title_floor_detection)
        MACHINE_MOVEMENT -> getString(R.string.ar_tutorial_title_machine_movement)
        WALL_DETECTION -> getString(R.string.ar_tutorial_title_wall_detection)
        MACHINE_DIMENSIONS_MOVEMENT -> getString(R.string.ar_tutorial_title_machine_dimensions_movement)
        else -> null
    }

}
