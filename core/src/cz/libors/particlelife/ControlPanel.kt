package cz.libors.particlelife

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.Separator
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSlider
import com.kotcrab.vis.ui.widget.VisTable


class ControlPanel(
    input: InputMultiplexer,
    val physicsActions: PhysicsActions
) {

    private val stage: Stage = Stage(ScreenViewport());
    private val fpsLabel: VisLabel
    private val physicsPsLabel: VisLabel

    init {
        input.addProcessor(stage)
        VisUI.load()
        fpsLabel = VisLabel()
        physicsPsLabel = VisLabel()
        mainPanel()
    }

    private fun mainPanel() {
        val table = VisTable()
        //table.debug()
        table.defaults().pad(3f)

        table.add(VisLabel("Particles")).row()

        table.add(Separator()).fillX().row();
        table.add(physicsPsLabel).row()
        table.add(physics()).row()

        table.add(Separator()).fillX().row()
        table.add(fpsLabel).row()
        table.add(graphics())

        table.top().left()
        table.setFillParent(true)
        table.pack()
        stage.addActor(table)
    }

    private fun physics(): VisTable {
        val table = VisTable()
        table.defaults().pad(5f)
        Slider(
            "Friction", 0f, 1f, 0.01f, 0.9f, 2,
            onUpdate = physicsActions::updateFriction
        ).attach(table)
        Slider("R Max", 30f, 300f, 1f, 50f, 0).attach(table)
        Slider("Pressure", 0.1f, 15f, 0.1f, 1f, 1).attach(table)
        return table
    }

    private fun graphics(): VisTable {
        val table = VisTable()
        table.defaults().pad(5f).left()
        Slider("Particle size", 1f, 10f, 0.5f, 3f, 1, onUpdate = { x -> println(x) }).attach(table)
        return table
    }

    fun render() {
        stage.act()
        physicsPsLabel.setText("Physics   (%d cps)".format(ParticleLife.physicsPs))
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

    class Slider(
        val name: String,
        val min: Float,
        val max: Float,
        val step: Float,
        val initValue: Float,
        val decimals: Int,
        val onUpdate: (Float) -> Unit = { }
    ) {

        fun attach(table: VisTable) {
            val sliderValue = VisLabel()
            val slider = VisSlider(min, max, step, false)
            slider.value = initValue
            slider.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    sliderValue.setText("%.${decimals}f".format(slider.value))
                    onUpdate(slider.value)
                }
            })
            sliderValue.setText("%.${decimals}f".format(slider.value))
            table.add(VisLabel(name))
            table.add(slider)
            table.add(sliderValue).minWidth(100f)
            table.row()
        }

    }
}