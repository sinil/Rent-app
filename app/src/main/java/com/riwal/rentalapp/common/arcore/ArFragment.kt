package com.riwal.rentalapp.common.arcore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.core.*
import com.google.ar.core.Config.FocusMode.*
import com.google.ar.core.Config.PlaneFindingMode.*
import com.google.ar.core.TrackingState.TRACKING
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node

class ArFragment : com.google.ar.sceneform.ux.ArFragment() {


    /*--------------------------------------- Properties -----------------------------------------*/


    var delegate: Delegate? = null

    private var previousTrackingTrackables: List<Trackable> = emptyList()

    private val currentTrackingTrackables
        get() = arSceneView.session?.getAllTrackables(Trackable::class.java)?.filter { it.trackingState == TRACKING }?.toList() ?: emptyList()

    private val newTrackingTrackables
        get() = currentTrackingTrackables - previousTrackingTrackables


    /*--------------------------------------- Lifecycle ------------------------------------------*/


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false
        return view
    }

    override fun onUpdate(frameTime: FrameTime) {
        super.onUpdate(frameTime)

        newTrackingTrackables.forEach { newTrackable ->

            if (newTrackable is Plane) {

                val planeAnchor = arSceneView.session!!.createAnchor(newTrackable.centerPose)
                val planeAnchorNode = PlaneAnchorNode(planeAnchor, newTrackable)
                arSceneView.scene.addChild(planeAnchorNode)

                delegate?.onNodeAddedForAnchor(this, node = planeAnchorNode, anchor = planeAnchor)

            }

        }

        delegate?.onUpdate(this, frameTime)

        previousTrackingTrackables = currentTrackingTrackables
    }

    override fun getSessionConfiguration(session: Session?): Config = super.getSessionConfiguration(session)
            .setPlaneFindingMode(HORIZONTAL_AND_VERTICAL)
            .setFocusMode(AUTO)


    /*-------------------------------------- Interfaces ------------------------------------------*/


    interface Delegate {
        fun onUpdate(arFragment: ArFragment, frameTime: FrameTime)
        fun onNodeAddedForAnchor(arFragment: ArFragment, node: Node, anchor: Anchor)
    }

}