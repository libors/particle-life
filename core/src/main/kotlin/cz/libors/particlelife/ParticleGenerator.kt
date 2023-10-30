package cz.libors.particlelife


object ParticleGenerator {

    fun createParticles(settings: Settings): List<Particle> {
        val genDef = LayoutSetup(
            Pair(ParticleLife.worldSize, ParticleLife.worldSize),
            settings.particleSetup.count,
            settings.particleSetup.types,
            settings.particleSetup.colonies
        )
        val layout = Layouts.getLayout(genDef, settings.particleSetup.layout)
        InitDelay.setup(settings.graphicsSetup.initDelay)
        return settings.particleSetup.count.toIntervals(settings.particleSetup.types)
            .flatMapIndexed { type, interval ->
                interval.map { layout.generate(type) }
            }
    }

    fun updateNum(settings: Settings, particles: List<Particle>, newCount: Int): List<Particle> {
        if (newCount == settings.particleSetup.count) {
            return particles
        }
        val layoutSetup = LayoutSetup(
            Pair(ParticleLife.worldSize, ParticleLife.worldSize),
            settings.particleSetup.count,
            settings.particleSetup.types,
            settings.particleSetup.colonies
        )
        val layout = Layouts.getLayout(layoutSetup, settings.particleSetup.layout)
        val origCount = settings.particleSetup.count
        settings.particleSetup.count = newCount
        return if (newCount > origCount) {
            val newParticles = (newCount - origCount).toIntervals(settings.particleSetup.types)
                .flatMapIndexed { type, interval ->
                    interval.map { layout.generate(type) }
                }
            particles + newParticles
        } else {
            particles.shuffled().subList(0, newCount)
        }
    }

}