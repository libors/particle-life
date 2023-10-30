package cz.libors.particlelife

import kotlin.math.sqrt
import kotlin.random.Random

interface Layout {
    fun generate(type: Int): Particle
}

data class LayoutSetup(
    val world: Pair<Int, Int>,
    val totalSize: Int,
    val types: Int,
    val near: Boolean
)

object Layouts {

    fun options() = listOf("random", "clusters", "spiral", "circle")

    fun getLayout(setup: LayoutSetup, id: String): Layout = when (id) {
        "random" -> RandomLayout(setup)
        "clusters" -> ClustersLayout(setup)
        "spiral" -> SpiralLayout(setup)
        "circle" -> CircleLayout(setup)
        else -> throw IllegalArgumentException("Unknown layout: $id")
    }
}

private sealed class AbstractLayout(protected val setup: LayoutSetup) : Layout

private class RandomLayout(def: LayoutSetup) : AbstractLayout(def) {
    override fun generate(type: Int): Particle {
        return Particle(
            if (!setup.near) Random.nextDouble(setup.world.first.toDouble()) else
                Random.nextDouble(setup.world.first.toDouble()) / setup.types + setup.world.first / setup.types * type,
            Random.nextDouble(setup.world.second.toDouble()),
            0.0, 0.0, type
        )
    }
}

private class ClustersLayout(def: LayoutSetup) : AbstractLayout(def) {
    val r = def.world.first / 3.0
    val colonyR = def.world.first / 8.0
    val center = Pair(setup.world.first / 2.0, setup.world.second / 2.0)
    val centers = if (setup.types == 1) listOf(center) else
        (0 until setup.types).map { center.pointOnCircle(r, 360.0 / setup.types * it) }

    override fun generate(type: Int): Particle {
        val idx = if (setup.near) type else Random.nextInt(setup.types)
        val c = centers[idx]
        val coords = c.pointOnCircle(sqrt(Random.nextDouble()) * colonyR, Random.nextDouble(360.0))
        return Particle(coords.first, coords.second, 0.0, 0.0, type)
    }
}

private class CircleLayout(def:LayoutSetup) : AbstractLayout(def) {
    val center = Pair(setup.world.first / 2.0, setup.world.second / 2.0)
    val r = def.world.first / 2.4
    override fun generate(type: Int): Particle {
        val portion = 360.0 / setup.types
        val angle = if (setup.near) Random.nextDouble(portion) + portion * type else Random.nextDouble(360.0)
        val coords = center.pointOnCircle(sqrt(Random.nextDouble()) * r, angle)
        return Particle(coords.first, coords.second, 0.0, 0.0, type)
    }
}

private class SpiralLayout(def: LayoutSetup) : AbstractLayout(def) {
    val center = Pair(setup.world.first / 2.0, setup.world.second / 2.0)
    val minR = def.world.first / 8.0
    val maxR = def.world.first / 3.0
    val minR2 = minR * minR
    val maxR2 = maxR * maxR
    val space = maxR - minR
    val maxAngle = 360 * 3

    override fun generate(type: Int): Particle {
        val dist = if (setup.near) {
            val portion2 = (maxR2 - minR2) / setup.types
            sqrt(Random.nextDouble(minR2 + portion2 * type, minR2 + portion2 * (type + 1)))
        } else minR + sqrt(Random.nextDouble(minR2, maxR2))
        val angle = dist / space * maxAngle
        val coords = center.pointOnCircle(dist, angle)
        return Particle(coords.first, coords.second, 0.0, 0.0, type)
    }
}
