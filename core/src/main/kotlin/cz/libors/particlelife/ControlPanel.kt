package cz.libors.particlelife

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.CollapsibleWidget
import com.kotcrab.vis.ui.widget.Separator
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisWindow


class ControlPanel(
    input: InputMultiplexer,
    private val settings: Settings
) {

    private val particles = settings.particleSetup
    private val physics = settings.physics
    private val graphics = settings.graphicsSetup

    private val stage: Stage = Stage(ScreenViewport());
    private val fpsLabel: VisLabel
    private val physicsPsLabel: VisLabel

    init {
        input.addProcessor(stage)
        VisUI.load()
        VisUI.setDefaultTitleAlign(Align.center)

        fpsLabel = VisLabel()
        physicsPsLabel = VisLabel()
        mainPanel()
    }

    private fun mainPanel() {
        val window = VisWindow("Control panel")
        //window.debug()
        window.add(panelContent()).expand().fill()
        window.pack()
        window.height += 50
        window.setPosition(20f, Gdx.graphics.height - window.height - 20)
        stage.addActor(window)
    }

    private fun panelContent(): VisTable {
        val table = VisTable()

        table.defaults().pad(3f)

        table.add(VisLabel("Particles")).row()
        table.add(particles()).row()

        table.add(Separator()).fillX().row();
        table.add(VisLabel("Start position")).row()
        table.add(layout()).row()

        table.add(Separator()).fillX().row();
        table.add(physicsPsLabel).row()
        table.add(physics()).row()

        table.add(Separator()).fillX().row()
        table.add(fpsLabel).row()
        table.add(graphics()).row()

        table.add(Separator()).fillX().row()
        table.add(VisLabel("Hand of god")).row()
        table.add(handOfGod()).row()

        table.add(Separator()).fillX().row()
        table.add(buttons()).row()
        table.pack()
        return table
    }

    private fun handOfGod(): VisTable {
        val table = VisTable()
        table.defaults().pad(5f)
        val collapsibleTable = VisTable()
        collapsibleTable.defaults().pad(5f)
        Slider("Reach", 5, 200, 5, settings.handOfGod.reach, 0, false) {
            settings.handOfGod.reach = it
        }.attach(collapsibleTable)
        Slider("Force", -15, 15, 1, settings.handOfGod.force, 0, false) {
            settings.handOfGod.force = it.toDouble()
        }.attach(collapsibleTable)
        val collapsible = CollapsibleWidget(collapsibleTable)
        collapsible.isCollapsed = true

        Check("Enable hand of god", settings.handOfGod.enabled) {
            settings.handOfGod.enabled = it
            collapsible.isCollapsed = !it
        }.attach(table).colspan(3).left().row()

        table.add(collapsible)
        return table
    }

    private fun buttons(): VisTable {
        val table = VisTable()
        table.defaults().pad(5f)
        Button("Restart", tooltip = "Restart with current configuration (Enter)") {
            ParticleLife.updateParticles { ParticleGenerator.createParticles(settings) }
        }.attach(table)
        Button("Regenerate", tooltip = "Restart with new random values (R)") {
            ParticleTypesConfigurer.generateMatrices(particles, physics)
            ParticleLife.updateParticles { ParticleGenerator.createParticles(settings) }
        }.attach(table)
        return table
    }

    private fun layout(): VisTable {
        val table = VisTable()
        table.defaults().pad(5f)
        Slider(
            "Init delay (s)", 0, 5, 0.2, graphics.initDelay, 1,
            tooltip = "How long to stay still after restart before particles start moving"
        ) {
            graphics.initDelay = it
        }.attach(table)
        SelectBox("Layout", Layouts.options(), particles.layout, tooltip = "The shape particles are spawned in") {
            particles.layout = it
        }.attach(table)
        table.row()
        Check(
            "Type colonies",
            particles.colonies,
            tooltip = "Whether particles of the same type should be spawned besides each other"
        ) {
            particles.colonies = it
        }.attach(table).colspan(2).left()
        return table
    }

    private fun particles(): VisTable {
        val table = VisTable()
        table.defaults().pad(5f)
        Slider(
            "Types", 1, 10, 1, particles.types, 0, manualApply = true,
            tooltip = "Number of different types of particles"
        ) { v ->
            ParticleLife.updateParticles { p -> ParticleTypesConfigurer.update(settings, p, v.toInt()) }
        }.attach(table)
        Slider(
            "Count", 500, 20000, 500, particles.count, 0, manualApply = true,
            tooltip = "Total number of all particles of all types"
        ) { v ->
            ParticleLife.updateParticles { p -> ParticleGenerator.updateNum(settings, p, v.toInt()) }
        }.attach(table)

        return table
    }

    private fun physics(): VisTable {
        val table = VisTable()
        table.defaults().pad(5f)
        Slider("Friction", 0, 1, 0.01, physics.friction, 2) {
            physics.friction = it.toDouble()
        }.attach(table)
        Slider("Force distance", 10, 300, 1, physics.forceR, 0) {
            physics.forceR = it.toDouble()
        }.attach(table)
        Slider("Force factor", 0.5, 10, 0.5, physics.forceFactor, 1) {
            physics.forceFactor = it.toDouble()
        }.attach(table)
        Slider("Pressure", 0.1, 15, 0.1, physics.pressure, 1) {
            physics.pressure = it.toDouble()
        }.attach(table)
        Slider("Pressure distance (%)", 5, 50, 1, physics.pressureRatio * 100, 0) {
            physics.pressureRatio = it.toDouble() / 100
        }.attach(table)
        Slider("Time interval", 0.05, 1, 0.05, physics.timeInterval, 2) {
            physics.timeInterval = it.toDouble()
        }.attach(table)
        val cpus = Runtime.getRuntime().availableProcessors()
        Slider("Threads", 1f, cpus, 1, physics.threads, 0) {
            physics.threads = it.toInt()
        }.attach(table)
        Check("Repulsive edges", !physics.endless) {
            physics.endless = !it
        }.attach(table).colspan(3).left().row()
        return table
    }

    private fun graphics(): VisTable {
        val table = VisTable()
        table.defaults().pad(5f).left()
        Slider("Particle size", 0.5, 5, 0.1, graphics.particleSize, 1) {
            graphics.particleSize = it
        }.attach(table)
        SelectBox("Color schema", ColorSchemas.options(), graphics.colorSchema) { graphics.colorSchema = it }.attach(
            table
        )
        table.row()
        return table
    }

    fun render() {
        stage.act()

        physicsPsLabel.setText("Physics   (%d %%)".format(PhysicsSpeed.percentPerSecond()))
        fpsLabel.setText("Graphics   (%d fps)".format(Gdx.graphics.framesPerSecond))
        stage.draw()
    }

    fun close() {
        stage.dispose()
        VisUI.dispose()
    }

    fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
}