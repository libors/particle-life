package cz.libors.particlelife

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

fun mod(num: Double, mod: Int): Double {
    val result = num % mod
    return if(result < 0) result + mod else result
}

fun mod(num: Int, mod: Int): Int {
    val result = num % mod
    return if (result < 0) result + mod else result
}