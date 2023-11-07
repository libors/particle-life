package cz.libors.particlelife

import com.badlogic.gdx.graphics.Color
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

interface ColorSchema {
    fun assign(p: Particle): Color
    fun schema(): List<Color>
}

object ColorSchemas {
    fun options() = listOf("default", "speed")

    fun getSchema(id: String): ColorSchema = when (id) {
        "default" -> TypeColorSchema(
            "#E74C3C",
            "#3498DB",
            "#F1C40F",
            "#1C8200",
            "#F38828",
            "#B0D894",
            "#286275",
            "#21C8E7",
            "#1BB89A",
            "#616B75"
        )

        "speed" -> SpeedColorSchema(3.0, "#000030", "#0000ff", "#00ffff", "#00ff00", "#ffff00", "#ff0000")
        else -> throw IllegalArgumentException("Unknown color schema: $id")
    }

}

private class TypeColorSchema(vararg colorStrings: String) : ColorSchema {
    private val colors = colorStrings.map { Color.valueOf(it + "FF") }

    init {
        require(colors.size >= 10) { "At least 10 colors should be provided" }
    }

    override fun assign(p: Particle) = colors[p.type]
    override fun schema() = colors.toList()
}

private class SpeedColorSchema(val max: Double, vararg colorsStrings: String) : ColorSchema {
    private val colors = colorsStrings.map { Color.valueOf(it + "FF") }
    private val oneRange = max / (colors.size - 1)
    private val schemaTicks = 150

    override fun assign(p: Particle): Color {
        val speed = min(sqrt(p.vx * p.vx + p.vy * p.vy), max)
        return (assign(speed))
    }

    fun assign(speed: Double): Color {
        val speed = round(speed, 3)
        if (max == speed) return colors[colors.size - 1]
        if (0.0 == speed) return colors[0]

        val idx1 = (speed / oneRange).toInt()
        val c1 = colors[idx1]
        val c2 = except( { colors[idx1 + 1] }, speed, max)
        val ratio = round(((speed % oneRange) / oneRange), 3).toFloat()

        return Color(c1.r + (c2.r - c1.r) * ratio, c1.g + (c2.g - c1.g) * ratio, c1.b + (c2.b - c1.b) * ratio, 1f)
    }

    override fun schema(): List<Color> {
        return (0 .. schemaTicks).map { assign(it / schemaTicks.toDouble() * max) }
    }
}

//fun main() {
//    val schema = SpeedColorSchema(3.0, "#000030", "#0000ff", "#00ffff", "#00ff00", "#ffff00", "#ff0000")
//    println(schema.schema())
//}
