package cz.libors.particlelife

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ExtendViewport

class ParticleDrawer(input: InputMultiplexer, val settings: Settings) {

    private val shape = ShapeRenderer()
    private val camera = OrthographicCamera(ParticleLife.worldSize.toFloat(), ParticleLife.worldSize.toFloat())
    private val viewport = ExtendViewport(ParticleLife.worldSize.toFloat() * 1.1f, ParticleLife.worldSize.toFloat() * 1.1f, camera)
    private val controller = WorldController(camera, settings)

    init {
        with (camera) {
            position.set(viewportWidth / 2f, viewportHeight / 2f , 0f);
            update();
        }
        input.addProcessor(controller)
    }

    fun draw(particles: List<Particle>) {
        val circleSegments = 18
        controller.updateCamera()
        camera.update()
        shape.projectionMatrix = camera.combined
        val width = ParticleLife.worldSize.toFloat()
        val height = ParticleLife.worldSize.toFloat()
        val endless = settings.physics.endless
        val particleRadius = settings.graphicsSetup.particleSize
        val colorSchema = ColorSchemas.getSchema(settings.graphicsSetup.colorSchema)
        with(shape) {
            begin(ShapeRenderer.ShapeType.Filled)
            for (p in particles) {
                color = colorSchema.assign(p)
                circle(p.x.toFloat(), p.y.toFloat(), particleRadius, circleSegments)
                if (endless) {
                    copyParticles(p, particleRadius, width, circleSegments, height)
                }
            }
            blackOverflowStripes(particleRadius, height, width)
            end()
            begin(ShapeRenderer.ShapeType.Line)
            color = Color.DARK_GRAY
            rect(0f, 0f, ParticleLife.worldSize.toFloat(), ParticleLife.worldSize.toFloat())
            end()
        }
        drawPalette(colorSchema, shape)
        drawHandOfGod(shape)

    }

    private fun drawHandOfGod(shape: ShapeRenderer) {
        if (settings.handOfGod.enabled) {
            with(shape) {
                begin(ShapeRenderer.ShapeType.Line)
                val mouseCoords = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
                camera.unproject(mouseCoords)
                val x = mouseCoords.x
                val y = mouseCoords.y
                val r = settings.handOfGod.reach
                val rp = r / 4
                color = Color.GRAY
                circle(x, y, r, 30)
                circle(x, y, rp, 30)
                line(x - rp, y, x + rp, y)
                line(x, y - rp, x, y + rp)
                end()
            }
        }
    }

    private fun drawPalette(colorSchema: ColorSchema, shape: ShapeRenderer) {
        val width = 300f
        val height = 10f
        val colors = colorSchema.schema()
        val colWidth = width / colors.size
        with(shape) {
            begin(ShapeRenderer.ShapeType.Filled)
            for (i in colors.indices) {
                color = colors[i]
                rect(10f + colWidth * i, 10f, colWidth, height)
            }
            end()
        }
    }

    private fun ShapeRenderer.blackOverflowStripes(
        particleRadius: Float,
        height: Float,
        width: Float
    ) {
        color = Color.BLACK
        rect(
            -particleRadius * 2,
            -particleRadius * 2,
            ParticleLife.worldSize.toFloat() + 4 * particleRadius,
            particleRadius * 2
        )
        rect(-particleRadius * 2, height, width + 4 * particleRadius, 2 * particleRadius)
        rect(-particleRadius * 2, -particleRadius * 2, particleRadius * 2, height + 4 * particleRadius)
        rect(width, -particleRadius * 2, particleRadius * 2, width + particleRadius * 2)
    }

    private fun ShapeRenderer.copyParticles(
        p: Particle,
        particleRadius: Float,
        width: Float,
        circleSegments: Int,
        height: Float
    ) {
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

    fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    fun close() {
        shape.dispose()
    }
}