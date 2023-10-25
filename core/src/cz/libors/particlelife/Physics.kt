package cz.libors.particlelife

import java.lang.IllegalStateException
import kotlin.math.*

class Physics(
    val maxWorldX: Int,
    val maxWorldY: Int,
    val physics: PhysicsValues
) {

    //private val bucketSize: Int = ceil(physics.distanceMatrix.map { it.max()!! }.max()!!).toInt()
    private val bucketSize = maxWorldX / 10
    private val xBuckets = maxWorldX / bucketSize
    private val yBuckets = maxWorldY / bucketSize

    init {
        if (maxWorldX % bucketSize > 0 || maxWorldY % bucketSize > 0) throw IllegalStateException()
    }

    fun update(particles: List<Particle>) {
        val grid: Array<Array<MutableList<Particle>>> =
            Array(xBuckets) { Array(yBuckets) { mutableListOf<Particle>() } }
        for (p in particles) {
            grid[p.x.toInt() / bucketSize][p.y.toInt() /bucketSize].add(p)
        }

        val deltaT = 0.2
        for (source in particles) {
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

            source.vx = source.vx * physics.friction + fx * deltaT
            source.vy = source.vy * physics.friction + fy * deltaT
        }
//        particles.forEach {p -> println(p)}
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
            for (target in subgrid[mod (j, yBuckets)]) {
                if (source != target) {
                    val yOffset = if (j < 0) -maxWorldY else if (j < yBuckets) 0 else maxWorldY
                    val rx = source.x - target.x - xOffset
                    val ry = source.y - target.y - yOffset
                    val maxDistance = physics.distanceMatrix[source.type][target.type]
                    val maxR = if (maxDistance == 0.0) physics.forceR else maxDistance
                    if (maxR == 0.0) throw IllegalStateException("maxR should not be 0")
                    if (rx <= maxR || ry <= maxR) {
                        val dist = sqrt(rx * rx + ry * ry)
                        val force = physics.forceMatrix[source.type][target.type]
                        if (dist > 0 && dist < maxR) {
//                            println("adding between $source and $target")
                            val fr = computeForce(dist, force, maxR) / dist
                            fx += fr * rx
                            fy += fr * ry
                        }
                    }
                }
            }
        }
        return Pair(fx, fy)
    }

    private fun computeForce(r: Double, f: Double, maxR: Double): Double {
        val closeR = 10
        if (r <= closeR) {
            return physics.pressure * ((closeR - r) / closeR)
        }
        val topD = closeR + (maxR - closeR) / 2.0
        if (r < topD) {
            val risingMax = topD - closeR
            return f * (r - closeR) / risingMax
        } else {
            val fallingMax = maxR - topD
            return f * (fallingMax - (r - topD)) / fallingMax
        }
    }
}