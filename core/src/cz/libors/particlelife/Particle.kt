package cz.libors.particlelife

class Particle(
    var x: Double,
    var y: Double,
    var vx: Double,
    var vy: Double,
    val type: Int


) {
    override fun toString(): String {
        return "Particle(x=$x, y=$y, vx=$vx, vy=$vy, type=$type)"
    }
}