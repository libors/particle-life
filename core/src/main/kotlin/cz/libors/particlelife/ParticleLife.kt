package cz.libors.particlelife

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.WindowedMean
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.max
import kotlin.math.min

class ParticleLife : ApplicationAdapter() {

    private var controlPanel: ControlPanel? = null
    private var particleDrawer: ParticleDrawer? = null
    private var engine: PhysicsEngine? = null
    private var settings: Settings? = null
    private var input = InputMultiplexer()

    companion object {
        private var particles: List<Particle>? = null

        val worldSize = 1000
        var stopped = false

        fun updateParticles(particleFun: (List<Particle>) -> List<Particle>) {
            this.particles = particleFun(this.particles!!)
        }

    }

    override fun create() {
        Gdx.input.inputProcessor = input
        val physicsSetup = PhysicsSetup(threads = max(Runtime.getRuntime().availableProcessors() - 1, 1))

        settings = Settings(configureParticles(physicsSetup), physicsSetup, GraphicsSetup(), HandOfGodSetup())
        controlPanel = ControlPanel(input, settings!!)
        particleDrawer = ParticleDrawer(input, settings!!)
        particles = ParticleGenerator.createParticles(settings!!)
        engine = createEngine(settings!!)
    }

    private fun configureParticles(physics: PhysicsSetup): ParticleSetup {
        val setup = ParticleSetup()
        ParticleTypesConfigurer.generateMatrices(setup, physics)
        return setup
    }

    private fun createEngine(settings: Settings): PhysicsEngine {
        return PhysicsEngine(
            maxWorldX = worldSize,
            maxWorldY = worldSize,
            physics = settings.physics,
            particleSettings = settings.particleSetup,
            handOfGod = settings.handOfGod
        )
    }

    override fun resize(width: Int, height: Int) {
        particleDrawer?.resize(width, height)
        controlPanel?.resize(width, height);
    }

    override fun render() {
        val delayStopped = InitDelay.shouldWait()
        val physicsTime = if (stopped || delayStopped) 0 else measure { engine?.update(particles!!) }
        val graphicsTime = measure {
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            particleDrawer?.draw(particles!!)
            controlPanel?.render()
        }
        PhysicsSpeed.update(physicsTime, graphicsTime)
    }

    override fun dispose() {
        particleDrawer?.close()
        controlPanel?.close()
        engine?.close()
    }

}