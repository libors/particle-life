package cz.libors.particlelife

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import kotlin.math.max
import kotlin.math.min

class Zooming(
    private val cam: OrthographicCamera,
    private val periodMs: Int = 300,
    private val step: Float = 0.1f,
    private val max: Float = 0.02f,
    private val min: Float = 1f
) {

    private var zoom: Zoom? = null

    fun update() {
        if (zoom != null) {
            with(zoom!!) {
                val tDelta = (System.currentTimeMillis() - t0) / periodMs.toFloat()
                cam.zoom = orig + zDelta * shape.apply(min(tDelta, 1f))
                if (tDelta >= 1f) zoom = null
            }
        }
    }

    fun change(amount: Float) {
        if (amount == 0f) return
        val delta = amount * step
        val now = System.currentTimeMillis()
        val allowed = if (amount < 0) max - cam.zoom else min - cam.zoom
        val d = if (amount < 0) max(delta, allowed) else min(delta, allowed)
        val shape = if (allowed == d) Interpolation.bounceOut else Interpolation.circleOut
        if (d == 0f) return
        zoom = Zoom(cam.zoom, d, now, shape)
    }

    private data class Zoom(val orig: Float, val zDelta: Float, val t0: Long, val shape: Interpolation)
}