package cz.libors.particlelife

data class PhysicsValues(
    val friction: Double,
    val forceFactor: Double,
    val forceR: Double,
    val pressure: Double,
    val pressureRatio: Double,
    val distanceMatrix: Array<DoubleArray>,
    val forceMatrix: Array<DoubleArray>,
    val endless: Boolean = false

)