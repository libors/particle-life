package cz.libors.particlelife

import kotlin.random.Random

object ParticleTypesConfigurer {

    fun generateMatrices(particleSetup: ParticleSetup, physics: PhysicsSetup) {
        val forceMatrix = Array(particleSetup.types) { DoubleArray(particleSetup.types) { 0.0 } }
        for (i in 0 until particleSetup.types)
            for (j in 0 until particleSetup.types)
                forceMatrix[i][j] = randomForce()
        val distanceMatrix = Array(particleSetup.types) { DoubleArray(particleSetup.types) { physics.forceR } }
        particleSetup.distanceMatrix = distanceMatrix
        particleSetup.forceMatrix = forceMatrix
    }

    private fun randomForce() = round(dir() * Random.nextDouble() * 0.6, 3)

    private fun dir() = when (Random.nextInt(3)) {
        0 -> 1
        1 -> -1
        else -> 0
    }

    fun update(settings: Settings, particles: List<Particle>, newTypes: Int): List<Particle> {
        if (newTypes < 0 || newTypes > 10) throw IllegalArgumentException("Cannot have $newTypes types of particles.")
        if (newTypes == settings.particleSetup.types) {
            return particles
        }
        settings.particleSetup.types = newTypes
        generateMatrices(settings.particleSetup, settings.physics)
        return particles.size.toIntervals(newTypes).flatMapIndexed { type, interval ->
            interval.map {
                val orig = particles[it]
                Particle(orig.x, orig.y, orig.vx, orig.vy, type)
            }
        }
    }

}