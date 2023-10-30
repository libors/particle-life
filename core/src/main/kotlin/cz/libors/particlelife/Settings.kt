package cz.libors.particlelife

class PhysicsSetup(
    var friction: Double = 0.2,
    var forceFactor: Double = 1.0,
    var forceR: Double = 30.0,
    var pressure: Double = 1.0,
    var pressureRatio: Double = 0.2,
    var endless: Boolean = true,
    var timeInterval: Double = 0.2,
    var threads: Int
)

class ParticleSetup(
    var types: Int = 5,
    var count: Int = 5000,
    var layout: String = "random",
    var colonies: Boolean = false,
    var distanceMatrix: Array<DoubleArray> = Array(0) { DoubleArray(0) },
    var forceMatrix: Array<DoubleArray> = Array(0) { DoubleArray(0) },
)

data class GraphicsSetup(
    var initDelay: Float = 0f,
    var particleSize: Float = 1f,
    var colorSchema: String = "default"
)

data class HandOfGodSetup(
    var enabled: Boolean = false,
    var reach: Float = 30f,
    var force: Double = 5.0
)

data class Settings(
    val particleSetup: ParticleSetup,
    val physics: PhysicsSetup,
    val graphicsSetup: GraphicsSetup,
    val handOfGod: HandOfGodSetup
)
