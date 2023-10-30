package cz.libors.particlelife

import com.badlogic.gdx.math.Vector2
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

inline fun measure(x: () -> Unit): Long {
    val start = System.currentTimeMillis()
    x()
    val end = System.currentTimeMillis()
    return end - start
}

inline fun <T> except(x: () -> T, vararg p: Any): T {
    try {
        return x()
    } catch (ex: Exception) {
        println("*************************************")
        for (item in p) {
            println("ERROR PARAM: $item")
        }
        throw ex
    }
}

fun Int.toIntervals(count: Int): List<IntRange> {
    val perPart = ceil(this / count.toDouble()).toInt()
    return (0 until count).map { it * perPart until min((it + 1) * perPart, this) }
}

fun round(value: Double, places: Int): Double {
    require(places >= 0)
    var bd = BigDecimal.valueOf(value)
    bd = bd.setScale(places, RoundingMode.HALF_UP)
    return bd.toDouble()
}

fun Pair<Double, Double>.pointOnCircle(r: Double, angle: Double) =
    Pair(first + r * cos(Math.toRadians(angle)), second + r * sin(Math.toRadians(angle)))

fun mod(num: Double, mod: Int): Double {
    val result = num % mod
    return if (result < 0) result + mod else result
}

fun mod(num: Int, mod: Int): Int {
    val result = num % mod
    return if (result < 0) result + mod else result
}