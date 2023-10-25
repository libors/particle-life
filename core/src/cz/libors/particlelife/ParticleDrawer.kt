package cz.libors.particlelife

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.ExtendViewport

class ParticleDrawer(input: InputMultiplexer, val endless: Boolean) {

    private val shape = ShapeRenderer()
    private val camera = OrthographicCamera(ParticleLife.worldSize.toFloat(), ParticleLife.worldSize.toFloat())
    private val viewport = ExtendViewport(ParticleLife.worldSize.toFloat() * 1.1f, ParticleLife.worldSize.toFloat() * 1.1f, camera)
    private val controller = WorldController(camera)

    private val colors = arrayOf(Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.GOLD, Color.MAGENTA)

    private val particleRadius= 1.5f

    init {
        with (camera) {
            position.set(viewportWidth / 2f, viewportHeight / 2f , 0f);
            update();
        }
        input.addProcessor(controller)
    }

    fun draw(particles: List<Particle>) {
        val circleSegments = 10
        controller.updateCamera()
        camera.update()
        shape.projectionMatrix = camera.combined
        val width = ParticleLife.worldSize.toFloat()
        val height = ParticleLife.worldSize.toFloat()
        with(shape) {
            begin(ShapeRenderer.ShapeType.Filled);
            for (p in particles) {
                color = colors[p.type]
                circle(p.x.toFloat(), p.y.toFloat(), particleRadius, circleSegments)
                if (endless) {
                    if (p.x < particleRadius) {
                        circle(p.x.toFloat() + width, p.y.toFloat(), particleRadius, circleSegments)
                    }
                    if (p.x > width - particleRadius) {
                        circle(p.x.toFloat() - width, p.y.toFloat(), particleRadius, circleSegments)
                    }
                    if (p.y < particleRadius) {
                        circle(p.x.toFloat(), p.y.toFloat() + height, particleRadius, circleSegments)
                    }
                    if (p.y > height - particleRadius) {
                        circle(p.x.toFloat(), p.y.toFloat() - height, particleRadius, circleSegments)
                    }
                }
            }

            color = Color.BLACK
            rect(-particleRadius * 2, -particleRadius * 2, ParticleLife.worldSize.toFloat() + 4 * particleRadius, particleRadius * 2)
            rect(-particleRadius * 2, height, width + 4 * particleRadius, 2 * particleRadius)
            rect(-particleRadius * 2, -particleRadius * 2, particleRadius * 2, height + 4 * particleRadius)
            rect(width, -particleRadius * 2, particleRadius * 2, width + particleRadius * 2)
            end()
            begin(ShapeRenderer.ShapeType.Line)
            color = Color.DARK_GRAY
            rect(0f, 0f, ParticleLife.worldSize.toFloat(), ParticleLife.worldSize.toFloat())
            end()
        }
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    fun close() {
        shape.dispose()
    }
}