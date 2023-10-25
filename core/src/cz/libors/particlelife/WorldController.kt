package cz.libors.particlelife

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import kotlin.math.min

class WorldController(val cam: OrthographicCamera) : InputAdapter() {

    private var moving = false
    private val zooming = Zooming(cam)

    private var sizeBeforeFs = Pair(1000, 600)

    fun updateCamera() {
        zooming.update()
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        zooming.change(amountY)
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        moving = true
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        moving = false
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (moving) {
            val x = Gdx.input.deltaX.toFloat()
            val y = Gdx.input.deltaY.toFloat()
            cam.translate(-x * cam.zoom, y * cam.zoom)

        }
        return true
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE -> {
                Gdx.app.exit()
                return true
            }

            Input.Keys.SPACE -> {
                ParticleLife.stopped = !ParticleLife.stopped
                return true
            }

            Input.Keys.F -> {
                if (Gdx.graphics.isFullscreen) {
                    Gdx.graphics.setWindowedMode(sizeBeforeFs.first, sizeBeforeFs.second)
                } else {
                    val displayMode = Gdx.graphics.getDisplayMode();
                    sizeBeforeFs = Pair(Gdx.graphics.width, Gdx.graphics.height)
                    Gdx.graphics.setFullscreenMode(displayMode)
                }
                return true
            }

            else -> {
                return false
            }
        }
    }

}