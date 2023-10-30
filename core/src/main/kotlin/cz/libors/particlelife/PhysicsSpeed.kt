package cz.libors.particlelife

import com.badlogic.gdx.math.WindowedMean

object PhysicsSpeed {

    private var physicsPercent = WindowedMean(10)
    private var physicsPercentLast = 0
    private var physicsPercentWait = 0

    fun update(physicsTime: Long, otherTime: Long) {
        val percent = 60 * physicsTime / (1000.0 - 60.0 * otherTime)
        physicsPercent.addValue((percent.toFloat() * 100))
    }

    fun percentPerSecond(): Int {
        if (physicsPercentWait++ % 20 == 0) {
            physicsPercentLast = physicsPercent.mean.toInt()
        }
        return physicsPercentLast
    }
}