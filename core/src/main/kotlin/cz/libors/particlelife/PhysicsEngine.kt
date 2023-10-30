package cz.libors.particlelife

import java.lang.IllegalStateException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.*

class PhysicsEngine(
    val maxWorldX: Int,
    val maxWorldY: Int,
    val physics: PhysicsSetup,
    val particleSettings: ParticleSetup,
    val handOfGod: HandOfGodSetup
) {

    private val bucketSize = 25
    private val xBuckets = maxWorldX / bucketSize
    private val yBuckets = maxWorldY / bucketSize
    private val executor = Executors.newCachedThreadPool()

    init {
        if (maxWorldX % bucketSize > 0 || maxWorldY % bucketSize > 0) throw IllegalStateException()
    }

    fun close() {
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.SECONDS)
    }

    fun update(particles: List<Particle>) {
        val grid: Array<Array<MutableList<Particle>>> =
            Array(xBuckets) { Array(yBuckets) { mutableListOf() } }
        for (p in particles) {
            grid[p.x.toInt() / bucketSize][p.y.toInt() / bucketSize].add(p)
        }

        val deltaT = physics.timeInterval
        if (particles.size < 1000) {
            for (source in particles) {
                updateVelocity(source, grid, deltaT)
            }
        } else {
            val tasks = particles.size.toIntervals(physics.threads).map {
                executor.submit {
                    for (particleIdx in it) {
                        updateVelocity(particles[particleIdx], grid, deltaT)
                    }
                }
            }
            tasks.forEach { it.get() }
        }

        particles.forEach { p ->
            p.x += p.vx * deltaT
            p.y += p.vy * deltaT
            if (!physics.endless) {
                handleBoundaries(p)
            } else {
                p.x = mod(p.x, maxWorldX)
                p.y = mod(p.y, maxWorldY)
            }
        }
    }

    private fun updateVelocity(
        source: Particle,
        grid: Array<Array<MutableList<Particle>>>,
        deltaT: Double
    ) {
        var fx = 0.0
        var fy = 0.0

        val left = floor((source.x - physics.forceR) / bucketSize).toInt()
        val right = (source.x + physics.forceR).toInt() / bucketSize
        val leftBound = if (physics.endless) left else max(0, left)
        val rightBound = if (physics.endless) right else min(right, xBuckets - 1)
        for (i in leftBound..rightBound) {
            val xOffset = if (i < 0) -maxWorldX else if (i < xBuckets) 0 else maxWorldX
            val partial = forceVectorForSource(source, grid[mod(i, xBuckets)], xOffset)
            fx += partial.first
            fy += partial.second
        }

        if (!physics.endless) {
            fx += boundaryForce(source.x)
            fx -= boundaryForce(maxWorldX - source.x)
            fy += boundaryForce(source.y)
            fy -= boundaryForce(maxWorldY - source.y)
        }

        source.vx = source.vx * (1 - physics.friction) + fx * deltaT
        source.vy = source.vy * (1 - physics.friction) + fy * deltaT
    }

    private fun boundaryForce(dist: Double): Double {
        val forceDist = 50
        return if (dist < forceDist)
            2.0 + (forceDist - dist) / forceDist * 8.0
        else
            0.0
    }

    private fun handleBoundaries(p: Particle) {
        if (p.x <= 0.0 || p.x >= maxWorldX) p.vx *= -1
        if (p.y <= 0.0 || p.y >= maxWorldY) p.vy *= -1
        if (p.x < 0.0) p.x = 0.0
        if (p.x > maxWorldX) p.x = maxWorldX.toDouble()
        if (p.y < 0.0) p.y = 0.0
        if (p.y > maxWorldY) p.y = maxWorldY.toDouble()
    }

    private fun forceVectorForSource(
        source: Particle,
        subgrid: Array<MutableList<Particle>>,
        xOffset: Int
    ): Pair<Double, Double> {
        var fx = 0.0
        var fy = 0.0
        val up = floor((source.y - physics.forceR) / bucketSize).toInt()
        val down = (source.y + physics.forceR).toInt() / bucketSize
        val upBound = if (physics.endless) up else max(0, up)
        val downBound = if (physics.endless) down else min(down, xBuckets - 1)
        for (j in upBound..downBound) {
            for (target in subgrid[mod(j, yBuckets)]) {
                if (source != target) {
                    val yOffset = if (j < 0) -maxWorldY else if (j < yBuckets) 0 else maxWorldY
                    val rx = source.x - target.x - xOffset
                    val ry = source.y - target.y - yOffset
                    //val maxDistance = particleSettings.distanceMatrix[source.type][target.type]
                    val maxDistance = physics.forceR
                    val maxR = if (maxDistance == 0.0) physics.forceR else maxDistance
                    if (maxR == 0.0) throw IllegalStateException("maxR should not be 0")
                    if (rx <= maxR || ry <= maxR) {
                        val dist = sqrt(rx * rx + ry * ry)
                        val force = particleSettings.forceMatrix[source.type][target.type] * physics.forceFactor
                        if (dist > 0 && dist < maxR) {
                            val fr = computeForce(dist, force, maxR) / dist
                            fx += fr * rx
                            fy += fr * ry
                        }
                    }
                }
            }
        }
        if (HandOfGod.pressing) {
            val rx = source.x - HandOfGod.x
            val ry = source.y - HandOfGod.y
            val dist = sqrt(rx * rx + ry * ry)
            if (dist > 0 && dist < handOfGod.reach) {
                fx += handOfGod.force * 0.01 * rx
                fy += handOfGod.force * 0.01 * ry
            }
        }
        return Pair(fx, fy)
    }

    private fun computeForce(distance: Double, force: Double, forceDistance: Double): Double {
        val pressureDistance = forceDistance * physics.pressureRatio
        if (distance <= pressureDistance) {
            return physics.pressure * ((pressureDistance - distance) / pressureDistance)
        }
        val topD = pressureDistance + (forceDistance - pressureDistance) / 2.0
        if (distance < topD) {
            val risingMax = topD - pressureDistance
            return force * (distance - pressureDistance) / risingMax
        } else {
            val fallingMax = forceDistance - topD
            return force * (fallingMax - (distance - topD)) / fallingMax
        }
    }
}