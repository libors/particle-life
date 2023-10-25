package cz.libors.particlelife

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class ParticleLife : ApplicationAdapter() {

    private var controlPanel: ControlPanel? = null
    private var particleDrawer: ParticleDrawer? = null
    private var physics: Physics? = null
    private var input = InputMultiplexer()

    private val r = Random()
    private var particles: List<Particle>? = null
    private val physicsActions = PhysicsActions()

    companion object {
        var physicsPs = 0
        val worldSize = 200
        val particlesNum = 100
        val typesNum = 3
        var stopped = false
    }

    override fun create() {
        Gdx.input.inputProcessor = input
        controlPanel = ControlPanel(input, physicsActions)
        physics = createPhysics()
        particleDrawer = ParticleDrawer(input, physics!!.physics.endless)
        particles = createParticles()
    }

    private fun createParticles(): List<Particle> {
        val result = mutableListOf<Particle>()
        for (type in 0 until typesNum)
            for (num in 0 until particlesNum)
                result.add(Particle(r.nextInt(worldSize).toDouble(), r.nextInt(worldSize).toDouble(), 0.0, 0.0, type))
                //result.add(Particle(0.5, 0.0, 0.0, 0.0, type))

//        result.add(Particle(5.0, worldSize - 5.0, 0.0, 0.0, 0))
//        result.add(Particle(worldSize - 5.0, 5.0, 0.0, 0.0, 0))
        return result
    }

    private fun createPhysics(): Physics {
        val forceMatrix = Array(typesNum) { DoubleArray(typesNum) { 0.0 } }
        for (i in 0 until typesNum)
            for (j in 0 until typesNum)
                forceMatrix[i][j] = randomForce()
        //forceMatrix[0][0] = 2.0
        val distanceMatrix = Array(typesNum) { DoubleArray(typesNum) { 30.0 } }
        return Physics(
            maxWorldX = worldSize,
            maxWorldY = worldSize,
            physics = PhysicsValues(
            friction = 0.9,
            pressure = 1.0,
            pressureRatio = 0.2,
            forceMatrix = forceMatrix,
            forceFactor = 1.0,
            forceR = 30.0,
            distanceMatrix = distanceMatrix,
            endless = true)
        )
    }

    private fun randomForce() = round(dir() * r.nextDouble() * 0.6, 3)

    private fun round(value: Double, places: Int): Double {
        require(places >= 0)
        var bd = BigDecimal.valueOf(value)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    private fun dir() = when (r.nextInt(3)) {
        0 -> 1
        1 -> -1
        else -> 0
    }

    override fun resize(width: Int, height: Int) {
        particleDrawer?.resize(width, height)
        controlPanel?.resize(width, height);
    }

    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (!stopped) {
            val len = measure { physics?.update(particles!!) }
            physicsPs = len.toInt()
        }
        particleDrawer?.draw(particles!!)
        controlPanel?.render()
    }

    override fun dispose() {
        particleDrawer?.close()
        controlPanel?.close()
    }

}