package cz.libors.particlelife

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3

object HandOfGod {
    var pressing = false
    var x = 0f
    var y = 0f

    fun update(mouseX: Int, mouseY: Int, cam: OrthographicCamera) {
        val mouseCoords = Vector3(mouseX.toFloat(), mouseY.toFloat(), 0f)
        cam.unproject(mouseCoords)
        x = mouseCoords.x
        y = mouseCoords.y
    }
}