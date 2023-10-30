package cz.libors.particlelife

object InitDelay {

    private var alreadyDone = true
    private var targetTime =  0L

    fun setup(delay: Float) {
        targetTime = System.currentTimeMillis() + (delay * 1000).toLong()
        alreadyDone = false
    }

    fun shouldWait(): Boolean {
        if (alreadyDone) {
            return false
        }
        val time = System.currentTimeMillis()
        if (time >= targetTime) {
            alreadyDone = true
        }
        return !alreadyDone
    }

}